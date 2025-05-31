package com.PickOne.domain.notification.dto;

import com.PickOne.domain.notification.model.domain.Notification;
import java.time.LocalDateTime;

/**
 * 알림 데이터 전송 객체 (DTO)
 * record를 사용하여 불변성 보장
 */
public record NotificationDto(
        String id,
        Long recipientId,
        String type,
        String content,
        String status,
        String refEntityType,
        String refEntityId,
        LocalDateTime createdAt,
        LocalDateTime readAt
) {
    /**
     * 도메인 객체로부터 DTO 생성
     */
    public static NotificationDto fromDomain(Notification notification) {
        return new NotificationDto(
                notification.getId(),
                notification.getRecipientId(),
                notification.getType().name(),
                notification.getContent(),
                notification.getStatus().name(),
                notification.getRefEntityType(),
                notification.getRefEntityId(),
                notification.getCreatedAt(),
                notification.getReadAt()
        );
    }
}