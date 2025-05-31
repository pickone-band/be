package com.PickOne.global.redis;

import com.PickOne.domain.messaging.dto.MessageDto;
import com.PickOne.domain.notification.dto.NotificationDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisMessageSubscriber implements MessageListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String body = new String(message.getBody(), StandardCharsets.UTF_8);
            String channel = new String(pattern, StandardCharsets.UTF_8);

            if (channel.contains("message-topic")) {
                MessageDto msg = objectMapper.readValue(body, MessageDto.class);
                messagingTemplate.convertAndSendToUser(
                        msg.recipientId().toString(),
                        "/queue/messages",
                        msg
                );
            } else if (channel.contains("notification-topic")) {
                NotificationDto dto = objectMapper.readValue(body, NotificationDto.class);
                messagingTemplate.convertAndSendToUser(
                        dto.recipientId().toString(),
                        "/queue/notifications",
                        dto
                );
            }
        } catch (Exception e) {
            log.error("[RedisSubscriber] 메시지 수신 처리 실패", e);
        }
    }
}