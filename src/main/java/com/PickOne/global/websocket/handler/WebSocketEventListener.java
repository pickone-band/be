package com.PickOne.global.websocket.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

/**
 * 웹소켓 연결 이벤트 핸들러
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    private final SimpMessageSendingOperations messagingTemplate;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal user = headerAccessor.getUser();

        if (user != null) {
            log.info("사용자가 연결되었습니다: {}", user.getName());

            // 친구나 팀 멤버에게 사용자가 온라인 상태임을 알리거나
            // Redis/DB에서 온라인 상태를 업데이트할 수 있습니다
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Principal user = headerAccessor.getUser();

        if (user != null) {
            log.info("사용자가 연결을 해제했습니다: {}", user.getName());

            // 오프라인 상태로 업데이트하거나 다른 사용자들에게 알림
        }
    }
}