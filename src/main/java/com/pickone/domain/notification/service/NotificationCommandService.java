package com.pickone.domain.notification.service;

import com.pickone.domain.notification.dto.NotificationResponse;
import com.pickone.domain.notification.model.domain.NotificationType;

public interface NotificationCommandService {
  NotificationResponse sendNotification(Long userId, String message, NotificationType type);
  void markAsRead(String notificationId);
}
