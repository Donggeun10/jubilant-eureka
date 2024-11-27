package com.example.chatter.controller;

import com.example.chatter.entity.ChatMessage;
import com.example.chatter.service.ChatMessagePubService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
@Slf4j
public class StompController {

    private final ChatMessagePubService chatMessagePubService;

    @MessageMapping("/chat/{chatRoomId}")
    public void message(@DestinationVariable String chatRoomId, @Payload ChatMessage request) {

        log.info("chatRoomId: {}, message: {}, ChatMessageRequest: {}", chatRoomId, request.getMessage(), request);
        chatMessagePubService.publishMessage(request);
    }
}
