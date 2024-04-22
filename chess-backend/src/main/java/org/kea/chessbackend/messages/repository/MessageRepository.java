package org.kea.chessbackend.messages.repository;


import org.kea.chessbackend.messages.model.ChatMessage;
import org.springframework.data.mongodb.repository.DeleteQuery;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface MessageRepository extends ReactiveMongoRepository<ChatMessage, String> {

    @Query("{gameId:  ?0}")
    Flux<ChatMessage> findAllByChatroomIdOrderByCreatedDateAsc(String chatroomId);

    @DeleteQuery("{gameId: ?0}")
    Mono<Void> deleteAllByChatroomId(String chatroomId);
}
