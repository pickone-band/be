package com.pickone.domain.notification.mapper;

import com.pickone.domain.notification.dto.NotificationResponse;
import com.pickone.domain.notification.entity.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationMapper {

  public NotificationResponse toResponse(Notification notification) {
    if (notification == null) return null;

    return new NotificationResponse(
        notification.getId(),
        notification.getUserId(),
        notification.getMessage(),
        notification.getType(),
        notification.getStatus(),
        notification.getCreatedAt()
    );
  }
}
