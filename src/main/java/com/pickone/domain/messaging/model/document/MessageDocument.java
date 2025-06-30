package com.pickone.domain.messaging.model.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Document(collection = "messages")
public class MessageDocument {

  @Id
  private String id;

  private Long roomId;
  private Long senderId;
  private String content;
  private LocalDateTime sentAt;

  public MessageDocument(Long roomId, Long senderId, String content, LocalDateTime sentAt) {
    this.roomId = roomId;
    this.senderId = senderId;
    this.content = content;
    this.sentAt = sentAt;
  }
}
