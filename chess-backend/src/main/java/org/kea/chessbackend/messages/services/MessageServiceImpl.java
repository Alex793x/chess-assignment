package org.kea.chessbackend.messages.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kea.chessbackend.messages.model.ChatMessage;
import org.kea.chessbackend.messages.repository.MessageRepository;
import org.kea.chessbackend.messages.services.interfaces.IMessageService;
import org.springframework.context.annotation.Primary;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Primary
@RequiredArgsConstructor
@Slf4j
public class MessageServiceImpl implements IMessageService {

    private final MessageRepository messageRepository;

    @Override
    public Mono<Void> deleteById(String messageId) {
        return messageRepository.deleteById(messageId)
                .doOnSuccess(unused -> log.info("Message deleted"))
                .doOnError(throwable -> log.info("Couldn't delete message"));
    }

    @Override
    public Flux<ChatMessage> getMessagesByChatroomId(String gameId) {
        return messageRepository.findAllByChatroomIdOrderByCreatedDateAsc(gameId)
                .doOnError(throwable -> log.error(throwable.getMessage()));
    }

    @Override
    public Mono<ChatMessage> saveMessage(ChatMessage chatMessage) {
        return messageRepository.save(chatMessage)
                .doOnSuccess(message -> log.info("Message: {} has been saved in Mongo", message))
                .doOnError(throwable -> log.info("Message {} failed to be saved", chatMessage));
    }

}
