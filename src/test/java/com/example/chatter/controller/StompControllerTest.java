package com.example.chatter.controller;

import static java.util.concurrent.TimeUnit.SECONDS;

import com.example.chatter.entity.ChatMessage;
import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

@Slf4j
@SpringBootTest
class StompControllerTest {

    private WebSocketStompClient stompClient;
    private CompletableFuture<ChatMessage> completableFuture;

    @BeforeEach
    void setup() {
        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        completableFuture = new CompletableFuture<>();
    }

    @Test
    @DisplayName("Websocket 테스트 ")
    void testWebsocket() throws ExecutionException, InterruptedException, TimeoutException {

        String url = String.format("ws://localhost:%d/ws-stomp", 8080);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession stompSession = stompClient.connectAsync(url, new StompSessionHandlerAdapter() {}).get(5, SECONDS);

        Assertions.assertTrue(stompSession.isConnected());

        stompSession.subscribe("/sub/chat/8f4cf2cd-15a2-42b0-8c01-47129de32a0f", new ChatMessageStompFrameHandler());

        ChatMessage chatMessage = ChatMessage.builder()
            .type(ChatMessage.MessageType.TALK)
            .chatRoomId("8f4cf2cd-15a2-42b0-8c01-47129de32a0f")
            .sender("apple")
            .message("hello")
            .build();

        stompSession.send("/pub/chat/8f4cf2cd-15a2-42b0-8c01-47129de32a0f", chatMessage);

        ChatMessage data = completableFuture.get(10, SECONDS);

        log.debug("data: {}", data);
        Assertions.assertNotNull(data);

    }

    private class ChatMessageStompFrameHandler implements StompFrameHandler {

        @Override
        public Type getPayloadType(StompHeaders stompHeaders) {
            return ChatMessage.class;
        }

        @Override
        public void handleFrame(StompHeaders stompHeaders, Object o) {
            completableFuture.complete((ChatMessage) o);
        }

    }
}
