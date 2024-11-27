package com.example.chatter.service;

import com.example.chatter.entity.ChatMessage;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.topic.ITopic;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatMessagePubService {

    @Qualifier("hazelcastLocalInstance")
    private final HazelcastInstance hazelcastInstance;

    @Value("${app.topic.talk:chat-talk-topic}")
    private String talkTopic;

    @Value("${app.topic.enter:chat-enter-topic}")
    private String enterTopic;

    private ITopic<ChatMessage> chatTalkTopic;
    private ITopic<ChatMessage> chatEnterTopic;

    @PostConstruct
    private void init() {
        chatTalkTopic = hazelcastInstance.getTopic(talkTopic);
        chatEnterTopic = hazelcastInstance.getTopic(enterTopic);
    }

    // [1] 메시지를 동기로 Publish한다.
    public void publishMessage(ChatMessage message) {
        log.debug("Publishing message: {}", message);
        switch(message.getType()){
            case TALK:
                chatTalkTopic.publish(message);
                break;
            case ENTER, EXIT:
                chatEnterTopic.publish(message);
                break;
            default:
                break;
        }
    }

}
