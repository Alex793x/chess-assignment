package org.kea.chessbackend.config.mongo;

import com.mongodb.reactivestreams.client.MongoClient;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
public class MongoConnectionTester {

    private final MongoClient mongoClient;

    @PostConstruct
    public void testConnection() {
        Flux.from(mongoClient.listDatabaseNames())
                .subscribe(
                        dbName -> System.out.println("Found database: " + dbName),
                        error -> System.err.println("Error connecting to MongoDB: " + error.getMessage()),
                        () -> System.out.println("Successfully connected to MongoDB.")
                );
    }
}
