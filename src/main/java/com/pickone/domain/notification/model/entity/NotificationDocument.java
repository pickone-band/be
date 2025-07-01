package com.pickone.domain.notification.model.entity;

import com.pickone.domain.notification.model.domain.NotificationStatus;
import com.pickone.domain.notification.model.domain.NotificationType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Document(collection = "notifications")
public class NotificationDocument {

  @Id
  private String id;

  private Long userId;

  private String message;

  @Enumerated(EnumType.STRING)
  private NotificationType type;

  private LocalDateTime createdAt;

  @Enumerated(EnumType.STRING)
  private NotificationStatus status;

  public static NotificationDocument of(Long userId, String message, NotificationType type) {
    return new NotificationDocument(
        null,
        userId,
        message,
        type,
        LocalDateTime.now(),
        NotificationStatus.UNREAD
    );
  }

  public void markRead() {
    this.status = NotificationStatus.READ;
  }
}
