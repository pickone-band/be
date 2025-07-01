package com.pickone.domain.notification.model.mapper;

import com.pickone.domain.notification.dto.NotificationDto;
import com.pickone.domain.notification.model.entity.NotificationDocument;

public class NotificationMapper {
  public static NotificationDto toDto(NotificationDocument doc) {
    return new NotificationDto(
        doc.getId(),
        doc.getUserId(),
        doc.getMessage(),
        doc.getType(),
        doc.getStatus(),
        doc.getCreatedAt()
    );
  }
}
