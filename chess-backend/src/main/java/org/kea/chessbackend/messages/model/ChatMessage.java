package org.kea.chessbackend.messages.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(value = "chat_message")
public class ChatMessage {

    @Id
    String id;

    String userId;

    String textMessage;

    String gameId;

    @CreatedDate
    Instant createdDate;

    @LastModifiedDate
    Instant lastModifiedDate;

    @Version
    Long version;
}
