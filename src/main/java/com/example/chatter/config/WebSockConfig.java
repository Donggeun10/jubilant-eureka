package com.example.chatter.config;

import com.example.chatter.component.StompHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@RequiredArgsConstructor
@Configuration
@EnableWebSocketMessageBroker
public class WebSockConfig implements WebSocketMessageBrokerConfigurer {

    final StompHandler stompHandler;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // /ws-stomp로 연결하는 endpoint를 생성하고, CORS 허용
        registry
            .addEndpoint("/ws-stomp")
            .setAllowedOrigins("*");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // /pub로 시작되는 메시지가 message-handling methods로 라우팅 되어야 한다.
        registry.setApplicationDestinationPrefixes("/pub");
        // /sub, /topic, /queue 로 시작되는 메시지가 메시지 브로커로 라우팅 되어야 한다.
        registry.enableSimpleBroker("/sub", "/topic", "/queue");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompHandler);
    }

}