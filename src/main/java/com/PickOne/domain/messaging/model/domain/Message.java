package com.PickOne.domain.messaging.model.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a chat message in the system.
 * Messages are first-class objects following domain-driven design principles.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "id")
public class Message {
    private String id;
    private SenderId senderId;
    private RecipientId recipientId;
    private MessageContent content;
    private MessageStatus status;
    private LocalDateTime sentAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime readAt;

    private Message(String id, SenderId senderId, RecipientId recipientId,
                    MessageContent content, MessageStatus status,
                    LocalDateTime sentAt, LocalDateTime deliveredAt, LocalDateTime readAt) {
        this.id = id;
        this.senderId = senderId;
        this.recipientId = recipientId;
        this.content = content;
        this.status = status;
        this.sentAt = sentAt;
        this.deliveredAt = deliveredAt;
        this.readAt = readAt;
    }

    /**
     * Create a new message that has just been sent
     */
    public static Message create(Long senderId, Long recipientId, String content) {
        return new Message(
                UUID.randomUUID().toString(),
                new SenderId(senderId),
                new RecipientId(recipientId),
                new MessageContent(content),
                MessageStatus.SENT,
                LocalDateTime.now(),
                null,
                null
        );
    }

    /**
     * Recreate a message from persistence
     */
    public static Message from(String id, Long senderId, Long recipientId, String content,
                               MessageStatus status, LocalDateTime sentAt,
                               LocalDateTime deliveredAt, LocalDateTime readAt) {
        return new Message(
                id,
                new SenderId(senderId),
                new RecipientId(recipientId),
                new MessageContent(content),
                status,
                sentAt,
                deliveredAt,
                readAt
        );
    }

    /**
     * Mark the message as delivered
     */
    public Message markDelivered() {
        if (this.status.ordinal() < MessageStatus.DELIVERED.ordinal()) {
            return new Message(
                    this.id,
                    this.senderId,
                    this.recipientId,
                    this.content,
                    MessageStatus.DELIVERED,
                    this.sentAt,
                    LocalDateTime.now(),
                    this.readAt
            );
        }
        return this;
    }

    /**
     * Mark the message as read
     */
    public Message markRead() {
        if (this.status.ordinal() < MessageStatus.READ.ordinal()) {
            return new Message(
                    this.id,
                    this.senderId,
                    this.recipientId,
                    this.content,
                    MessageStatus.READ,
                    this.sentAt,
                    this.deliveredAt != null ? this.deliveredAt : LocalDateTime.now(),
                    LocalDateTime.now()
            );
        }
        return this;
    }

    public Long getSenderIdValue() {
        return this.senderId.getValue();
    }

    public Long getRecipientIdValue() {
        return this.recipientId.getValue();
    }

    public String getContentValue() {
        return this.content.getValue();
    }
}
