package com.pickone.domain.notification.service;

import com.pickone.domain.notification.dto.NotificationDto;
import java.util.List;

public interface NotificationQueryService {
  List<NotificationDto> getNotifications(Long userId);
}