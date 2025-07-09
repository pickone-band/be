package com.pickone.domain.notification.entity;

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
public class Notification {

  @Id
  private String id;

  private Long userId;

  private String message;

  @Enumerated(EnumType.STRING)
  private NotificationType type;

  private LocalDateTime createdAt;

  @Enumerated(EnumType.STRING)
  private NotificationStatus status;

  public static Notification create(Long userId, String message, NotificationType type) {
    return new Notification(
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
