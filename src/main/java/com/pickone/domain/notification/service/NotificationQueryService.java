package com.pickone.domain.notification.service;

import com.pickone.domain.notification.dto.NotificationResponse;

import java.util.List;

public interface NotificationQueryService {
  List<NotificationResponse> getNotifications(Long userId);
}
