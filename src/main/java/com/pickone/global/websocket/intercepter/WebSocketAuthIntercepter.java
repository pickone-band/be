package com.pickone.global.websocket.intercepter;

import com.pickone.global.security.config.SecurityConstants;
import com.pickone.global.security.token.TokenProvider;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketAuthIntercepter implements ChannelInterceptor {

  private final TokenProvider tokenProvider;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message,
        StompHeaderAccessor.class);

    if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
      List<String> authorizationHeaders = accessor.getNativeHeader(SecurityConstants.AUTH_HEADER);

      if (authorizationHeaders != null && !authorizationHeaders.isEmpty()) {
        String authHeader = authorizationHeaders.get(0);

        if (authHeader != null && authHeader.startsWith(SecurityConstants.TOKEN_PREFIX)) {
          String token = authHeader.substring(SecurityConstants.TOKEN_PREFIX.length());

          try {
            if (!tokenProvider.isTokenBlacklisted(token)) {
              Authentication auth = tokenProvider.getAuthentication(token);
              accessor.setUser((Principal) auth.getPrincipal());
              SecurityContextHolder.getContext().setAuthentication(auth);
              log.info("웹소켓 인증 성공: user={}", auth.getName());
            } else {
              log.warn("웹소켓 인증 실패 - 블랙리스트 토큰 사용");
              return null; // 메시지 거부
            }
          } catch (Exception e) {
            log.error("웹소켓 인증 처리 중 예외 발생: {}", e.getMessage(), e);
            return null; // 메시지 거부
          }
        } else {
          log.warn("웹소켓 인증 실패 - Authorization 헤더 형식 오류");
        }
      } else {
        log.warn("웹소켓 인증 실패 - Authorization 헤더 없음");
      }
    }

    return message;
  }
}
