package com.pickone.domain.messaging.model.document;

import jakarta.persistence.Id;
import java.time.LocalDateTime;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "message_reads")
public class MessageReadStatusDocument {

  @Id
  private String id;
  private Long roomId;
  private String messageId;
  private Long userId;
  private boolean isRead;
  private LocalDateTime readAt;
}
