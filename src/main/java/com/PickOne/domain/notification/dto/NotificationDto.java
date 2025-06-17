package com.PickOne.domain.notification.dto;

import com.PickOne.domain.notification.model.domain.NotificationStatus;
import com.PickOne.domain.notification.model.domain.NotificationType;

import java.time.LocalDateTime;

/**
 * 알림 데이터 전송 객체 (DTO)
 * record를 사용하여 불변성 보장
 */
public record NotificationDto(
        String id,
        Long recipientId,
        NotificationType type,
        String title,
        String content,
        NotificationStatus status,
        LocalDateTime createdAt,
        LocalDateTime readAt
) {}