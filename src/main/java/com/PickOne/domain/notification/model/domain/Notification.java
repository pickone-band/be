
package com.PickOne.domain.notification.model.domain;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 시스템의 알림
 */
@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class Notification {

    private final String id;
    private final Long recipientId;
    private final NotificationType type;
    private final String content;
    private final NotificationStatus status;
    private final String refEntityType;
    private final String refEntityId;
    private final LocalDateTime createdAt;
    private final LocalDateTime readAt;

    public static Notification create(Long recipientId, NotificationType type, String content, String refEntityType, String refEntityId) {
        return new Notification(
                null,
                recipientId,
                type,
                content,
                NotificationStatus.UNREAD,
                refEntityType,
                refEntityId,
                LocalDateTime.now(),
                null
        );
    }

    public Notification markAsRead() {
        return new Notification(
                id,
                recipientId,
                type,
                content,
                NotificationStatus.READ,
                refEntityType,
                refEntityId,
                createdAt,
                LocalDateTime.now()
        );
    }
}
