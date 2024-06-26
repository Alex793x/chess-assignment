package org.kea.chessbackend.openai.gpt.service.interfaces;

import org.kea.chessbackend.messages.model.ChatMessage;
import org.kea.chessbackend.messages.model.ChunkData;
import org.mvnsearch.chatgpt.model.ChatCompletion;
import org.mvnsearch.chatgpt.model.GPTExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@GPTExchange
public interface IGPT_General {

    @ChatCompletion("""
            As an intelligent assistant in a multi-user chatroom, your role is to interact with users by responding
            to their chatMessages. Each chatMessage in this chatroom is formatted with a user ID followed by 'said:',
            and then the chatMessage content, like '12345 said: Can you give me recipe ideas?'. Treat each user ID as
            the name of the person speaking.
            Prioritize responding to the newest chatMessages first, as these are the most
            immediate and relevant to the conversation. Address the user by their ID when
            replying, and offer helpful and informed responses to their questions or comments.
            Your aim is to engage with users by providing timely and contextually appropriate assistance,
            treating each user ID as a unique individual in the chat. Focus on the most recent queries to
            ensure the conversation is current and responsive to the latest inputs.
            """)
    Flux<String> streamChat(String question);

    @ChatCompletion("You are a helpful assistant.")
    Mono<String> chat(String question);

    Flux<ChunkData> streamChatContext(List<ChatMessage> chatMessages);
}
