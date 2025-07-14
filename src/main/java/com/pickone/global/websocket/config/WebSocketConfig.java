package com.pickone.global.websocket.config;

import com.pickone.global.websocket.intercepter.WebSocketAuthIntercepter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  private final WebSocketAuthIntercepter webSocketAuthIntercepter;

  @Override
  public void configureMessageBroker(MessageBrokerRegistry config) {
    log.info("[WebSocketConfig] configureMessageBroker 호출됨");
    config.enableSimpleBroker("/topic", "/queue");
    config.setApplicationDestinationPrefixes("/app");
    config.setUserDestinationPrefix("/user");
    log.info("[WebSocketConfig] 브로커 설정 완료: /topic, /queue, prefix=/app, /user");
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    log.info("[WebSocketConfig] registerStompEndpoints 호출됨");
    registry.addEndpoint("/ws")
        .setAllowedOriginPatterns("*")
        .withSockJS();
    log.info("[WebSocketConfig] STOMP endpoint '/ws' 등록 완료 (SockJS 지원)");
  }

  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    log.info("[WebSocketConfig] configureClientInboundChannel 호출됨");
    registration.interceptors(webSocketAuthIntercepter);
    log.info("[WebSocketConfig] WebSocket 인증 인터셉터 등록 완료");
  }

  @Override
  public boolean configureMessageConverters(List<MessageConverter> converters) {
    log.info("[WebSocketConfig] configureMessageConverters 호출됨");

    DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();
    resolver.setDefaultMimeType(MimeTypeUtils.APPLICATION_JSON);

    MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
    converter.setContentTypeResolver(resolver);
    converters.add(converter);

    log.info("[WebSocketConfig] JSON 메시지 컨버터 등록 완료");
    return false;
  }
}
