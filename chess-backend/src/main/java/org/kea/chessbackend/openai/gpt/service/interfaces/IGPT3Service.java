package org.kea.chessbackend.openai.gpt.service.interfaces;


import org.kea.chessbackend.messages.model.ChatMessage;
import org.kea.chessbackend.messages.model.ChunkData;
import org.mvnsearch.chatgpt.model.GPTExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@GPTExchange
public interface IGPT3Service extends IGPT_General {

    @Override
    Flux<String> streamChat(String question);

    @Override
    Mono<String> chat(String question);

    @Override
    Flux<ChunkData> streamChatContext(List<ChatMessage> chatMessages);





}
