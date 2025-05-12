
package com.PickOne.domain.notification.model.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a notification in the system.
 * Notifications are first-class objects following domain-driven design principles.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "id")
public class Notification {
    private String id;
    private RecipientId recipientId;
    private NotificationType type;
    private NotificationContent content;
    private NotificationStatus status;
    private RefEntityId refEntityId;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;

    private Notification(String id, RecipientId recipientId, NotificationType type,
                         NotificationContent content, NotificationStatus status,
                         RefEntityId refEntityId, LocalDateTime createdAt, LocalDateTime readAt) {
        this.id = id;
        this.recipientId = recipientId;
        this.type = type;
        this.content = content;
        this.status = status;
        this.refEntityId = refEntityId;
        this.createdAt = createdAt;
        this.readAt = readAt;
    }

    /**
     * Create a new notification
     */
    public static Notification create(Long recipientId, NotificationType type,
                                      String content, String refEntityType, Long refEntityId) {
        return new Notification(
                UUID.randomUUID().toString(),
                new RecipientId(recipientId),
                type,
                new NotificationContent(content),
                NotificationStatus.UNREAD,
                new RefEntityId(refEntityType, refEntityId),
                LocalDateTime.now(),
                null
        );
    }

    /**
     * Recreate a notification from persistence
     */
    public static Notification from(String id, Long recipientId, NotificationType type,
                                    String content, NotificationStatus status,
                                    String refEntityType, Long refEntityId,
                                    LocalDateTime createdAt, LocalDateTime readAt) {
        return new Notification(
                id,
                new RecipientId(recipientId),
                type,
                new NotificationContent(content),
                status,
                new RefEntityId(refEntityType, refEntityId),
                createdAt,
                readAt
        );
    }

    /**
     * Mark the notification as read
     */
    public Notification markRead() {
        if (this.status == NotificationStatus.UNREAD) {
            return new Notification(
                    this.id,
                    this.recipientId,
                    this.type,
                    this.content,
                    NotificationStatus.READ,
                    this.refEntityId,
                    this.createdAt,
                    LocalDateTime.now()
            );
        }
        return this;
    }

    public Long getRecipientIdValue() {
        return this.recipientId.getValue();
    }

    public String getContentValue() {
        return this.content.getValue();
    }

    public String getRefEntityType() {
        return this.refEntityId.getType();
    }

    public Long getRefEntityIdValue() {
        return this.refEntityId.getValue();
    }
}
