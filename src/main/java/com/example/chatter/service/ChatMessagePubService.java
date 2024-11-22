package com.example.chatter.service;

import com.example.chatter.entity.ChatMessage;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.topic.ITopic;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatMessagePubService {

    @Qualifier("hazelcastLocalInstance")
    private final HazelcastInstance hazelcastInstance;

    ITopic<ChatMessage> chatTopic;

    @PostConstruct
    private void init() {
        chatTopic = hazelcastInstance.getTopic("chat-topic");
    }

    // [1] 메시지를 동기로 Publish한다.
    public void publishMessage(ChatMessage message) {
        log.debug("Publishing message: {}", message);
        chatTopic.publish(message);
    }

}
