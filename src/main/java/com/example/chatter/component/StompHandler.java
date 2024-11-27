package com.example.chatter.component;

import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StompHandler implements ChannelInterceptor {

    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {

        if(log.isDebugEnabled()) {

            StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
            String sessionId = accessor.getSessionId();

            switch (Objects.requireNonNull(accessor.getCommand())) {
                case CONNECT -> log.debug("CONNECT: {}", message);
                case CONNECTED -> log.debug("CONNECTED: {}", message);
                case DISCONNECT -> log.debug("DISCONNECT: {}", message);
                case SUBSCRIBE -> log.debug("SUBSCRIBE: {}", message);
                case UNSUBSCRIBE -> log.debug("UNSUBSCRIBE: {}", sessionId);
                case SEND -> log.debug("SEND: {}", sessionId);
                case MESSAGE -> log.debug("MESSAGE: {}", sessionId);
                case ERROR -> log.debug("ERROR: {}", sessionId);
                default -> log.debug("UNKNOWN: {}", sessionId);
            }

        }
    }
}
