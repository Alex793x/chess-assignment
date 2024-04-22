package org.kea.chessbackend.openai.gpt.service;

import lombok.extern.slf4j.Slf4j;
import org.kea.chessbackend.messages.model.ChatMessage;
import org.kea.chessbackend.messages.model.ChunkData;
import org.kea.chessbackend.openai.gpt.service.interfaces.IGPT3Service;
import org.kea.chessbackend.openai.utility.UtilityMethods;
import org.mvnsearch.chatgpt.model.completion.chat.ChatCompletionRequest;
import org.mvnsearch.chatgpt.model.completion.chat.ChatCompletionResponse;
import org.mvnsearch.chatgpt.spring.service.ChatGPTService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Primary
@Service
@Slf4j
public class GPT3ServiceImpl implements IGPT3Service {

    private final String AI_MODEL_ID = "1L";

    private final ChatGPTService chatGPTService;

    public GPT3ServiceImpl(ChatGPTService chatGPTService) {
        this.chatGPTService = chatGPTService;
    }

    @Override
    public Mono<String> chat(String content) {
        return chatGPTService.chat(ChatCompletionRequest.of(content))
                .map(ChatCompletionResponse::getReplyText);
    }

    @Override
    public Flux<String> streamChat(String question) {
        var chatCompletionRequest = ChatCompletionRequest.of(question);
        chatCompletionRequest.setMaxTokens(1000);
        chatCompletionRequest.setTemperature(1.0);
        chatCompletionRequest.setModel("gpt-3.5-turbo-1106");
        return chatGPTService.stream(chatCompletionRequest)
                .map(ChatCompletionResponse::getReplyText);
    }


    @Override
    public Flux<ChunkData> streamChatContext(List<ChatMessage> chatMessages) {
        // Process chatMessages to build the chat completion request
        var chatCompletionRequest = buildChatCompletionRequest(chatMessages);
        for (org.mvnsearch.chatgpt.model.completion.chat.ChatMessage chatMessage : chatCompletionRequest.getMessages()) {
            log.info("Message added: {}", chatMessage.getContent());
        }

        String gameId = chatMessages.getFirst().getGameId();
        String gptMessageId = UUID.randomUUID().toString();
        StringBuilder gptAnswer = new StringBuilder();
        Instant gptCreatedAnswer = Instant.now().plusSeconds(5);
        AtomicInteger totalChunks = new AtomicInteger(0);

        ChunkData initialResponse = UtilityMethods.awaitingResponse(gptMessageId, "1L", gameId);

        Mono<ChunkData> finalChunkMono = Mono.fromCallable(() -> {
            ChatMessage completeMessage = ChatMessage.builder()
                    .id(gptMessageId)
                    .gameId(gameId)
                    .userId("1L")
                    .textMessage(gptAnswer.toString())
                    .gameId(gameId)
                    .createdDate(gptCreatedAnswer)
                    .build();
            return ChunkData.of(gptMessageId, completeMessage, (long) gptAnswer.length(), (long) totalChunks.get(), true); // Mark as the final chunk
        });

        Flux<ChunkData> gptResponse = chatGPTService.stream(chatCompletionRequest)
                .map(ChatCompletionResponse::getReplyText)
                .map(chunk -> {
                    synchronized (gptAnswer) {
                        gptAnswer.append(chunk);
                        totalChunks.set(totalChunks.get() + 1);
                    }
                    ChatMessage newMessage = ChatMessage.builder()
                            .id(gptMessageId)
                            .userId("1L")
                            .textMessage(chunk)
                            .gameId(gameId)
                            .createdDate(gptCreatedAnswer)
                            .lastModifiedDate(gptCreatedAnswer)
                            .build();

                    log.info(chunk);

                    return ChunkData.of(gptMessageId, newMessage, (long) chunk.length(), null, false);
                })
                .publishOn(Schedulers.boundedElastic())
                .doOnError(error -> {
                    // Log and handle error
                    System.err.println("Error in processing stream: " + error.getMessage());
                })
                .concatWith(finalChunkMono);

        return Flux.concat(Flux.just(initialResponse), gptResponse.delaySubscription(Duration.ofSeconds(2)));
    }

    private ChatCompletionRequest buildChatCompletionRequest(List<ChatMessage> chatMessages) {
        var chatCompletionRequest = new ChatCompletionRequest();
        chatCompletionRequest.addMessage(org.mvnsearch.chatgpt.model.completion.chat.ChatMessage.systemMessage(gptInstruction()));

        chatMessages.getLast().setCreatedDate(Instant.now());

        int tokenCount = 0;
        int maxTokens = 32_000;
        Set<String> addedMessageIds = new HashSet<>();

        for (ChatMessage chatMessage : chatMessages) {
            if (!addedMessageIds.contains(chatMessage.getId())) {
                String context = getMessageContext(chatMessage);
                tokenCount += context.length();

                if (tokenCount > maxTokens) { // Check if adding this message exceeds the limit
                    break; // Exit the loop immediately
                }

                if (context.toLowerCase().contains("@gpt")) {
                    String gptQuestion = context.replace("@gpt", "");
                    chatCompletionRequest.addMessage(org.mvnsearch.chatgpt.model.completion.chat.ChatMessage.userMessage(gptQuestion));
                } else if (chatMessage.getUserId().equals(AI_MODEL_ID)) {
                    chatCompletionRequest.addMessage(org.mvnsearch.chatgpt.model.completion.chat.ChatMessage.assistantMessage(context));
                } else {
                    chatCompletionRequest.addMessage(org.mvnsearch.chatgpt.model.completion.chat.ChatMessage.userMessage(context));
                }
                log.info("Adding chatMessage: " + chatMessage);
                addedMessageIds.add(chatMessage.getId());
            }
        }

        chatCompletionRequest.setMaxTokens(4096);
        chatCompletionRequest.setTemperature(1.0);
        chatCompletionRequest.setModel("gpt-3.5-turbo-0125");
        return chatCompletionRequest;
    }

    public String getMessageContext(ChatMessage chatMessage) {
        String userId = chatMessage.getUserId();
        if (isAnotherAiOrUserMessage(userId)) {
            return "[" + userId + " | " + chatMessage.getCreatedDate() + "]: " + chatMessage.getTextMessage();
        } else {
            return chatMessage.getTextMessage();
        }
    }

    public boolean isAnotherAiOrUserMessage(String userId) {
        return !userId.equals("1L") && userId.length() < 4;
    }


    private String gptInstruction() {
        return """
                You are an AI designed to participate in a multi-user chatroom environment.
                                
                This chatroom includes both human users and multiple AI entities. Human users are identified by
                24-character-long IDs, while AI entities have IDs that are less than 4 characters long.
                                
                Your primary role is to provide helpful, informative, and contextually relevant responses to user queries and discussions.
                The chatroom hosts dynamic conversations among multiple users on a variety of topics. Conversations can shift rapidly, and new messages may alter the context or introduce new discussion threads.
                While it's crucial to consider the overall context of the conversation, you should prioritize the most recent messages to maintain relevance and timeliness in your responses.
                Ensure to maintain a coherent thread of conversation, integrating recent inputs while not disregarding the broader context established by earlier messages.
                All your responses must adhere to high ethical standards, promoting positive and respectful interactions. Avoid generating content that could be harmful, offensive, or inappropriate.
                """;
    }


}
