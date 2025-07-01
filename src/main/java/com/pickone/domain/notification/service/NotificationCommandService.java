package com.pickone.domain.notification.service;

import com.pickone.domain.notification.dto.NotificationDto;

public interface NotificationCommandService {
  NotificationDto sendNotification(Long userId, String message, String type);
  void markAsRead(String notificationId);
}
