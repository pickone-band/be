package com.PickOne.domain.messaging.model.domain;

import lombok.*;

import java.time.LocalDateTime;

/**
 * 채팅 메시지
 */
/**
 * 메시지 도메인 모델 (단순화: SenderId/RecipientId/Content → 일반 필드)
 */
@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class Message {
    private final String id;
    private final Long senderId;
    private final Long recipientId;
    private final String content;
    private final MessageStatus status;
    private final LocalDateTime sentAt;
    private final LocalDateTime deliveredAt;
    private final LocalDateTime readAt;

    public static Message create(Long senderId, Long recipientId, String content) {
        return new Message(
                null,
                senderId,
                recipientId,
                content,
                MessageStatus.SENT,
                LocalDateTime.now(),
                null,
                null
        );
    }

    public Message markAsDelivered() {
        return new Message(id, senderId, recipientId, content, MessageStatus.DELIVERED, sentAt, LocalDateTime.now(), readAt);
    }

    public Message markAsRead() {
        return new Message(id, senderId, recipientId, content, MessageStatus.READ, sentAt, deliveredAt, LocalDateTime.now());
    }
}
