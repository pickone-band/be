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

  private String messageId;
  private Long userId;
  private LocalDateTime readAt;

  public MessageReadStatusDocument(String messageId, Long userId, LocalDateTime readAt) {
    this.messageId = messageId;
    this.userId = userId;
    this.readAt = readAt;
  }

}
