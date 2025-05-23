package com.PickOne.global.websocket.interceptor;

import com.PickOne.global.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            List<String> authorizationHeaders = accessor.getNativeHeader("Authorization");

            if (authorizationHeaders != null && !authorizationHeaders.isEmpty()) {
                String authHeader = authorizationHeaders.get(0);

                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    String token = authHeader.substring(7);

                    try {
                        if (!jwtService.isTokenBlacklisted(token)) {
                            Authentication auth = jwtService.getAuthentication(token);
                            accessor.setUser(auth);
                            SecurityContextHolder.getContext().setAuthentication(auth);
                            log.debug("웹소켓 연결이 인증되었습니다: {}", auth.getName());
                        } else {
                            log.warn("블랙리스트에 등록된 토큰으로 웹소켓 연결 요청이 거부되었습니다");
                            return null; // 메시지 거부
                        }
                    } catch (Exception e) {
                        log.error("웹소켓 인증에 실패했습니다: {}", e.getMessage());
                        return null; // 메시지 거부
                    }
                } else {
                    log.warn("적절한 Authorization 헤더가 없는 웹소켓 연결 요청입니다");
                }
            } else {
                log.warn("Authorization 헤더가 없는 웹소켓 연결 요청입니다");
            }
        }

        return message;
    }
}