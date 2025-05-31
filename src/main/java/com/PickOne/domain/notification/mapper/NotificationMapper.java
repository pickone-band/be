package com.PickOne.domain.notification.mapper;


import com.PickOne.domain.notification.dto.NotificationDto;
import com.PickOne.domain.notification.model.domain.Notification;
import com.PickOne.domain.notification.model.domain.NotificationStatus;
import com.PickOne.domain.notification.model.domain.NotificationType;
import com.PickOne.domain.notification.model.entity.NotificationDocument;

public class NotificationMapper {

    public static Notification toDomain(NotificationDocument doc) {
        return new Notification(
                doc.getId(),
                doc.getRecipientId(),
                NotificationType.valueOf(doc.getType()),
                doc.getContent(),
                NotificationStatus.valueOf(doc.getStatus()),
                doc.getRefEntityType(),
                doc.getRefEntityId(),
                doc.getCreatedAt(),
                doc.getReadAt()
        );
    }

    public static NotificationDocument toDocument(Notification domain) {
        return new NotificationDocument(
                domain.getId(),
                domain.getRecipientId(),
                domain.getType().name(),
                domain.getContent(),
                domain.getStatus().name(),
                domain.getRefEntityType(),
                domain.getRefEntityId(),
                domain.getCreatedAt(),
                domain.getReadAt()
        );
    }

    public static NotificationDto toDto(Notification domain) {
        return new NotificationDto(
                domain.getId(),
                domain.getRecipientId(),
                domain.getType().name(),
                domain.getContent(),
                domain.getStatus().name(),
                domain.getRefEntityType(),
                domain.getRefEntityId(),
                domain.getCreatedAt(),
                domain.getReadAt()
        );
    }
}
