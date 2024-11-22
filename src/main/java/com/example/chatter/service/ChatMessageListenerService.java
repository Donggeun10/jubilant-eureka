package com.example.chatter.service;

import com.example.chatter.entity.ChatMessage;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.topic.ITopic;
import com.hazelcast.topic.Message;
import com.hazelcast.topic.MessageListener;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatMessageListenerService implements MessageListener<ChatMessage> {

    @Qualifier("hazelcastLocalInstance")
    private final HazelcastInstance hazelcastInstance;
    private final SimpMessagingTemplate simpMessagingTemplate;

    ITopic<ChatMessage> chatTopic;

    @PostConstruct
    private void init() {
        chatTopic = hazelcastInstance.getTopic("chat-topic");
        chatTopic.addMessageListener(this);

    }

    @Override
    public void onMessage(Message<ChatMessage> message) {
        log.info("Received: " + message.getMessageObject());
        ChatMessage chatMessage = message.getMessageObject();
        long chatRoomId = chatMessage.getChatRoomId();
        simpMessagingTemplate.convertAndSend("/sub/chat/" + chatRoomId, chatMessage);
    }

}
