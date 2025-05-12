package com.PickOne.global.websocket.handler;

import com.PickOne.domain.messaging.dto.MessageDto;
import com.PickOne.domain.notification.dto.NotificationDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * 채팅 메시지를 위한 Redis 메시지 리스너
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RedisMessageSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            // Redis에서 메시지 역직렬화
            String channel = new String(pattern);
            String messageBody = new String(message.getBody());

            if (channel.endsWith("messaging")) {
                // 채팅 메시지 처리
                MessageDto messageDto = objectMapper.readValue(messageBody, MessageDto.class);

                // 특정 사용자에게 전송
                String destination = "/queue/messages";
                messagingTemplate.convertAndSendToUser(
                        String.valueOf(messageDto.recipientId()),
                        destination,
                        messageDto
                );

                log.debug("사용자 {}에게 메시지 전송: {}", messageDto.recipientId(), messageDto.id());
            } else if (channel.endsWith("notifications")) {
                // 알림 처리
                NotificationDto notificationDto = objectMapper.readValue(messageBody, NotificationDto.class);

                // 특정 사용자에게 전송
                String destination = "/queue/notifications";
                messagingTemplate.convertAndSendToUser(
                        String.valueOf(notificationDto.recipientId()),
                        destination,
                        notificationDto
                );

                log.debug("사용자 {}에게 알림 전송: {}", notificationDto.recipientId(), notificationDto.id());
            }
        } catch (Exception e) {
            log.error("Redis 메시지 처리 중 오류 발생: {}", e.getMessage(), e);
        }
    }
}