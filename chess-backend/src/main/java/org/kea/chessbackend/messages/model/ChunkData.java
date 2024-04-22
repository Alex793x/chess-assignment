package org.kea.chessbackend.messages.model;

public record ChunkData (
        String identifier,
        ChatMessage chunk,
        Long startIndex,
        Long totalChunks,
        Boolean isLastChunk

) {
    public static ChunkData of(String identifier, ChatMessage chunk, Long startIndex, Long totalChunks, Boolean isLastChunk) {
        return new ChunkData(identifier, chunk, startIndex, totalChunks, isLastChunk);
    }
}
