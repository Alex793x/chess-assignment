package org.kea.chessbackend.messages.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kea.chessbackend.messages.model.ChatMessage;
import org.kea.chessbackend.messages.model.ChunkData;
import org.kea.chessbackend.messages.services.ChunkDataProducer;
import org.kea.chessbackend.messages.services.interfaces.IMessageService;
import org.kea.chessbackend.openai.gpt.service.interfaces.IGPT3Service;
import org.kea.chessbackend.utilities.ReactiveRSocketUtilityMethods;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ConcurrentRSocketController {
    private final ObjectMapper objectMapper;
    private final ChunkDataProducer chunkDataProducer;
    private final IMessageService messageService;
    private final ReactiveRSocketUtilityMethods utilityMethods;
    private final IGPT3Service gpt3Service;
    private final Map<String, Sinks.Many<ChunkData>> chessGameRoomSinks = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> connectionToChatroomMap = new ConcurrentHashMap<>();
    private final Map<String, List<ChunkData>> chunkStream = new ConcurrentHashMap<>();

    @MessageMapping("chat.stream.{gameId}")
    public Flux<ChunkData> streamMessages(@DestinationVariable String gameId, Mono<String> requestMessage) {
        return requestMessage.thenMany(chessGameRoomSinks.computeIfAbsent(gameId, id ->
                        Sinks.many()
                                .replay()
                                .latest())
                .asFlux().onBackpressureBuffer());
    }


    @MessageMapping("chat.send.{gameId}")
    public Mono<Void> receiveMessage(@DestinationVariable String gameId, ChunkData chunkData) {
        // Store chunk in temporary storage
        chunkStream.computeIfAbsent(chunkData.identifier(), k -> new ArrayList<>()).add(chunkData);
        if (utilityMethods.isCompleteMessage(chunkData.identifier(), chunkStream)) {
            List<ChunkData> completeChunks = chunkStream.remove(chunkData.identifier());
            return processCompleteMessage(completeChunks, gameId);
        }
        return Mono.empty();
    }

    @MessageMapping("chat.send.leave.{gameId}")
    public Mono<Void> receiveLeaveMessage(@DestinationVariable String gameId) {
        connectionToChatroomMap.remove(gameId);
        chessGameRoomSinks.remove(gameId);
        chunkStream.remove(gameId);
        return Mono.empty();
    }

    @KafkaListener(id = "#{'chess-message-event-consumer-' + T(java.util.UUID).randomUUID().toString()}",
            topics = "chess-message-event",
            groupId = "#{'chess-backend' + T(java.util.UUID).randomUUID().toString()}")
    public void listen(String message,
                       @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                       @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        try {
            ChunkData chunkData = objectMapper.readValue(message, ChunkData.class);
            Sinks.Many<ChunkData> sink = chessGameRoomSinks.get(chunkData.chunk().getGameId());
            log.info(String.format("Consumed event from topic %s: key = %-10s value = %s", topic, key, message));
            if (sink != null) {
                sink.emitNext(chunkData, Sinks.EmitFailureHandler.FAIL_FAST);
            } else {
                // Sink not found for the given chat room ID, handle accordingly
                log.error("Sink not found for chess game room ID: {}", chunkData.chunk().getGameId());
                // Consider initializing a new sink, logging the error, or taking other appropriate action
            }
        } catch (JsonProcessingException e) {
            // Handle deserialization error
            log.error("Error deserializing message: {}", message, e);
            // Consider not acknowledging the message, so it can be retried according to your error handling policy
        }
    }


    private Mono<Void> processCompleteMessage(List<ChunkData> chunks, String chatroomId) {
        // Sort the chunks by startIndex to ensure they are in the correct order
        List<ChunkData> sortedChunks = chunks.stream()
                .sorted(Comparator.comparing(ChunkData::startIndex))
                .toList();
        if (utilityMethods.isGptMessage(sortedChunks) && utilityMethods.isLastChunkReceived(sortedChunks)) {
            return getAICompletionResponse(sortedChunks);
        }

        return processRegularMessage(sortedChunks.getFirst());
    }

    private Mono<Void> getAICompletionResponse(List<ChunkData> sortedChunks) {
        List<ChatMessage> messages = sortedChunks.stream()
                .map(ChunkData::chunk)
                .toList();

        return processRegularMessage(sortedChunks.getLast())
                .then(Mono.defer(() ->
                        gpt3Service.streamChatContext(messages)
                                .flatMap(chunkData -> {
                                    if (!chunkData.isLastChunk()) {
                                        chunkDataProducer.sendMessage(chunkData);
                                    }
                                    if (chunkData.isLastChunk()) {
                                        return saveMessageAndReadReceiptToDB(chunkData.chunk());
                                    }
                                    return Mono.empty();
                                }).then()

                ));
    }

    private Mono<Void> processRegularMessage(ChunkData messageChunk) {
        return emitReceivedMessage(messageChunk.chunk());
    }

    public Mono<Void> emitReceivedMessage(ChatMessage chatMessage) {
        Instant createdDate = Instant.now();
        ChatMessage receivedMessage = ChatMessage.builder()
                .id(chatMessage.getId())
                .userId(chatMessage.getUserId())
                .textMessage(chatMessage.getTextMessage())
                .createdDate(createdDate)
                .lastModifiedDate(createdDate)
                .gameId(chatMessage.getGameId())
                .build();
        ChunkData messageChunkData = ChunkData.of(UUID.randomUUID().toString(), receivedMessage, 0L, 1L, true);
        chunkDataProducer.sendMessage(messageChunkData);
        return saveMessageAndReadReceiptToDB(receivedMessage);
    }

    private Mono<Void> saveMessageAndReadReceiptToDB(ChatMessage receivedMessage) {
        return messageService.saveMessage(receivedMessage)
                .then();
    }
}
 