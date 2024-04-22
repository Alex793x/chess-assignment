package org.kea.chessbackend.messages.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kea.chessbackend.messages.model.ChunkData;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;


import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChunkDataProducer {

    private static final String TOPIC = "chess-message-event";
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendMessage(ChunkData chunkData) {
        try {
            String serializedData = objectMapper.writeValueAsString(chunkData);
            CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(TOPIC, chunkData.chunk().getGameId(), serializedData);

            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Failed to send message to topic {} for chess-gameId ID: {}", TOPIC, chunkData.chunk().getGameId(), ex);
                } else {
                    log.info(String.format("Produced event to topic %s: key = %-10s value = %s", TOPIC, chunkData.chunk().getGameId(), serializedData));
                }
            });
        } catch (JsonProcessingException e) {
            log.error("Error serializing ChunkData for chess-message-event ID: {}", chunkData.chunk().getGameId(), e);
        }
    }


}