package com.study.localmeet.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

// STOMP 기반 WebSocket 설정
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // 메시지 브로커 설정
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 클라이언트로 메시지를 보낼 때 prefix (/topic/meeting/1 으로 구독)
        registry.enableSimpleBroker("/topic");

        // 클라이언트에서 서버로 메시지 보낼 때 prefix (/app/chat/send 로 전송)
        registry.setApplicationDestinationPrefixes("/app");
    }

    // WebSocket 연결 엔드포인트 설정
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/chat")  // ws://localhost:8080/ws/chat 으로 연결
                .setAllowedOriginPatterns("*")
                .withSockJS();  // SockJS 폴백 지원 (브라우저 호환성)
    }
}
