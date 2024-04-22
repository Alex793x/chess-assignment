package org.kea.chessbackend.openai.utility;

import org.kea.chessbackend.messages.model.ChatMessage;
import org.kea.chessbackend.messages.model.ChunkData;

public class UtilityMethods {

    public static ChunkData awaitingResponse(String messageUUID, String aiId, String gameId) {
        return ChunkData.of(messageUUID,
                ChatMessage.builder()
                        .id(messageUUID)
                        .userId(aiId)
                        .gameId(gameId)
                        .textMessage("Awaiting Response")
                        .build(),
                -1L,
                null,
                false);
    }
}
