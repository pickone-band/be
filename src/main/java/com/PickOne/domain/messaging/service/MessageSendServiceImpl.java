package com.PickOne.domain.messaging.service;

import com.PickOne.domain.messaging.dto.MessageDto;
import com.PickOne.domain.messaging.mapper.MessageMapper;
import com.PickOne.domain.messaging.model.domain.Message;
import com.PickOne.domain.messaging.repository.MessageRepository;
import com.PickOne.domain.notification.dto.NotificationDto;
import com.PickOne.domain.notification.mapper.NotificationMapper;
import com.PickOne.domain.notification.model.domain.Notification;
import com.PickOne.domain.notification.model.domain.NotificationType;
import com.PickOne.domain.notification.service.NotificationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageSendServiceImpl {

    private final MessageRepository messageRepository;
    private final NotificationService notificationService;
    private final RedisTemplate<String, String> redisTemplate; // 타입 명확화
    private final ObjectMapper objectMapper;

    private static final String MESSAGE_TOPIC = "message-topic";
    private static final String NOTIFICATION_TOPIC = "notification-topic";

    public MessageDto sendMessage(Long senderId, Long recipientId, String content) {
        Message message = Message.create(senderId, recipientId, content);
        Message saved = messageRepository.save(message);

        String refId = Optional.ofNullable(saved.getId())
                .orElseThrow(() -> new IllegalStateException("메시지 ID가 null입니다."));

        Notification notification = notificationService.createNotification(
                recipientId,
                NotificationType.NEW_MESSAGE,
                NotificationType.NEW_MESSAGE.getDefaultMessage(),
                "MESSAGE",
                refId
        );

        publishToSocket(saved, notification);
        return MessageMapper.toDto(saved);
    }

    public void notifyOnly(Notification notification) {
        publishToSocket(null, notification);
    }

    private void publishToSocket(Message message, Notification notification) {
        try {
            if (message != null) {
                MessageDto dto = MessageMapper.toDto(message);
                redisTemplate.convertAndSend(MESSAGE_TOPIC, objectMapper.writeValueAsString(dto));
            }
            NotificationDto notiDto = NotificationMapper.toDto(notification);
            redisTemplate.convertAndSend(NOTIFICATION_TOPIC, objectMapper.writeValueAsString(notiDto));
        } catch (JsonProcessingException e) {
            log.error("[SocketPublisher] Redis Publish 실패", e);
        }
    }
}
