package com.pickone.domain.notification.dto;

import com.pickone.domain.notification.model.domain.NotificationStatus;
import com.pickone.domain.notification.model.domain.NotificationType;
import java.time.LocalDateTime;

public record NotificationDto(
    String id,
    Long userId,
    String message,
    NotificationType type,
    NotificationStatus status,
    LocalDateTime createdAt
) {}
