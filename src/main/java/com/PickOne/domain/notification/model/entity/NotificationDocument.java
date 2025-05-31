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
 * 알림 저장을 위한 MongoDB 문서
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

    private String refEntityId;

    private LocalDateTime createdAt;

    private LocalDateTime readAt;

}