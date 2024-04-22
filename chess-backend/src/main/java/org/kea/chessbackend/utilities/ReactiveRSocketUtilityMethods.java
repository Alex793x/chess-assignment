package org.kea.chessbackend.utilities;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kea.chessbackend.messages.model.ChatMessage;
import org.kea.chessbackend.messages.model.ChunkData;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


@Slf4j
@Component
@RequiredArgsConstructor
public class ReactiveRSocketUtilityMethods {

    public boolean isCompleteMessage(String identifier, Map<String, List<ChunkData>> chunkStream ) {
        List<ChunkData> chunks = chunkStream.get(identifier);
        if (chunks == null || chunks.isEmpty()) {
            return false;
        }

        long totalChunks = chunks.getFirst().totalChunks();
        return chunks.size() == totalChunks;
    }


    public boolean isGptMessage(List<ChunkData> chunkDataList) {
        ChatMessage chunk = chunkDataList.getLast().chunk();
        return chunk != null && chunk.getTextMessage().startsWith("@ChatGPT");
    }

    public boolean isLastChunkReceived(List<ChunkData> chunkDataList) {
        if (chunkDataList.size() == chunkDataList.getFirst().totalChunks()) {
            return chunkDataList.stream().anyMatch(ChunkData::isLastChunk);
        } else {
            return false;
        }
    }

}