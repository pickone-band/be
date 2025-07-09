package com.pickone.domain.messaging.document;

import jakarta.persistence.Id;
import java.time.LocalDateTime;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "message_reads")
public class MessageReadStatus {

  @Id
  private String id;

  private Long roomId;
  private String messageId;
  private Long userId;

  private boolean isRead;
  private LocalDateTime readAt;

  public static MessageReadStatus create(Long roomId, String messageId, Long userId, boolean isRead, LocalDateTime readAt) {
    MessageReadStatus status = new MessageReadStatus();
    status.roomId = roomId;
    status.messageId = messageId;
    status.userId = userId;
    status.isRead = isRead;
    status.readAt = readAt;
    return status;
  }
}
