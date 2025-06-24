package com.pickone.domain.notification.model.entity;

import com.pickone.domain.notification.model.domain.NotificationStatus;
import com.pickone.domain.notification.model.domain.NotificationType;
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

  private String title;

  private String content;

  private NotificationType type;

  private NotificationStatus status;

  @Indexed
  private LocalDateTime createdAt;

  private LocalDateTime readAt;

}