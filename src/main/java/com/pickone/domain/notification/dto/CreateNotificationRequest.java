package com.pickone.domain.notification.dto;

import com.pickone.domain.notification.model.domain.NotificationType;

public record CreateNotificationRequest(
    Long userId,
    String message,
    NotificationType type
) {}