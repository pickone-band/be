package com.pickone.domain.messaging.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PROTECTED)
@Document(collection = "messages")
public class Message {

  @Id
  private String id;

  private Long roomId;
  private Long senderId;
  private Long receiverId;

  private String content;
  private LocalDateTime sentAt;

  @Builder.Default
  private Set<Long> readUserIds = new HashSet<>();

  public static Message create(Long roomId, Long senderId, Long receiverId, String content, LocalDateTime sentAt) {
    return Message.builder()
        .roomId(roomId)
        .senderId(senderId)
        .receiverId(receiverId)
        .content(content)
        .sentAt(sentAt)
        .build();
  }

  public void markRead(Long userId) {
    this.readUserIds.add(userId);
  }

  public boolean isReadBy(Long userId) {
    return readUserIds.contains(userId);
  }
}
