package com.pickone.domain.notification.dto;

import com.pickone.domain.notification.model.domain.NotificationStatus;
import com.pickone.domain.notification.model.domain.NotificationType;

import java.time.LocalDateTime;

public record NotificationDto(
    String id,
    Long recipientId,
    NotificationType type,
    String title,
    String content,
    NotificationStatus status,
    LocalDateTime createdAt,
    LocalDateTime readAt
) {

}