package org.kea.chessbackend.messages.services.interfaces;

import org.kea.chessbackend.messages.model.ChatMessage;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IMessageService {

    Mono<Void> deleteById(String messageId);

    Flux<ChatMessage> getMessagesByChatroomId(String chatroomId);

    Mono<ChatMessage> saveMessage(ChatMessage chatMessage);
}
