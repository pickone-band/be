package com.PickOne.global.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.PickOne.global.websocket.interceptor.WebSocketAuthInterceptor;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketAuthInterceptor webSocketAuthInterceptor;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 단순한 인메모리 메시지 브로커 활성화
        config.enableSimpleBroker("/topic", "/queue");

        // 클라이언트에서 서버로 보내는 메시지의 접두사 정의
        config.setApplicationDestinationPrefixes("/app");

        // 사용자별 목적지의 접두사 정의
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 클라이언트가 웹소켓 서버에 연결할 때 사용할 STOMP 엔드포인트 등록
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // 인증 및 권한 부여를 위한 인터셉터 추가
        registration.interceptors(webSocketAuthInterceptor);
    }
}