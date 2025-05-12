
package com.PickOne.domain.notification.model.entity;

import com.PickOne.domain.notification.model.domain.Notification;
import com.PickOne.domain.notification.model.domain.NotificationStatus;
import com.PickOne.domain.notification.model.domain.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;

/**
 * MongoDB document for storing notifications
 */
@Document(collection = "notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDocument {

    @Id
    private String id;

    @Indexed
    private Long recipientId;

    private String type;

    private String content;

    private String status;

    private String refEntityType;

    private Long refEntityId;

    private LocalDateTime createdAt;

    private LocalDateTime readAt;

    /**
     * Convert from domain model to document
     */
    public static NotificationDocument fromDomain(Notification notification) {
        return NotificationDocument.builder()
                .id(notification.getId())
                .recipientId(notification.getRecipientIdValue())
                .type(notification.getType().name())
                .content(notification.getContentValue())
                .status(notification.getStatus().name())
                .refEntityType(notification.getRefEntityType())
                .refEntityId(notification.getRefEntityIdValue())
                .createdAt(notification.getCreatedAt())
                .readAt(notification.getReadAt())
                .build();
    }

    /**
     * Convert to domain model
     */
    public Notification toDomain() {
        return Notification.from(
                id,
                recipientId,
                NotificationType.valueOf(type),
                content,
                NotificationStatus.valueOf(status),
                refEntityType,
                refEntityId,
                createdAt,
                readAt
        );
    }
}
