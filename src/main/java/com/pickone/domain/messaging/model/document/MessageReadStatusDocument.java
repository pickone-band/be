package com.pickone.domain.messaging.model.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Document(collection = "message_reads")
public class MessageReadStatusDocument {

  @Id
  private String id;

  private Long roomId;
  private String messageId;
  private Long userId;
  private boolean isRead;
  private LocalDateTime readAt;

  public MessageReadStatusDocument(Long roomId, String messageId, Long userId, boolean isRead, LocalDateTime readAt) {
    this.roomId = roomId;
    this.messageId = messageId;
    this.userId = userId;
    this.isRead = isRead;
    this.readAt = readAt;
  }

}
