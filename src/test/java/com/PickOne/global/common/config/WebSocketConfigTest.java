package com.PickOne.global.common.config;

import com.PickOne.domain.notification.dto.NotificationDto;
import com.PickOne.global.security.service.JwtService;
import com.PickOne.global.websocket.handler.RedisMessageSubscriber;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
public class WebSocketConfigTest {

    @Autowired
    private RedisMessageSubscriber redisMessageSubscriber;

    @MockBean
    private SimpMessagingTemplate messagingTemplate;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    public void testRedisMessageSubscriber() {
        assertNotNull(redisMessageSubscriber);

        // 메시지 구독 테스트를 위한 Mock 설정
        // 실제 메시지 전송은 하지 않음

        System.out.println("RedisMessageSubscriber loaded successfully");
    }

    @Test
    public void testWebSocketAuthInterceptor() {
        // JwtService 모킹
        when(jwtService.isTokenBlacklisted(anyString())).thenReturn(true);
        when(jwtService.getAuthentication(anyString())).thenReturn(null);

        System.out.println("JwtService mock set up successfully");
    }
}