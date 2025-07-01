package com.pickone.domain.messaging.model.document;

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
public class MessageDocument {

  @Id
  private String id;

  private Long roomId;           // 채팅방 식별자 (필수)
  private Long senderId;         // 메시지 발신자

  // 1:1 채팅일 때만 사용, 단톡은 null 또는 미사용
  private Long receiverId;

  private String content;
  private LocalDateTime sentAt;

  // 참여자별 읽음 여부 (그룹채팅 확장)
  @Builder.Default
  private Set<Long> readUserIds = new HashSet<>();

  // 메시지 생성: 1:1 or 단톡 공통 사용
  public static MessageDocument of(Long roomId, Long senderId, Long receiverId, String content, LocalDateTime sentAt) {
    return MessageDocument.builder()
        .roomId(roomId)
        .senderId(senderId)
        .receiverId(receiverId)
        .content(content)
        .sentAt(sentAt)
        .build();
  }

  // 읽음 처리 (개별 참여자 기준)
  public void markRead(Long userId) {
    this.readUserIds.add(userId);
  }

  // 특정 참여자 기준 읽음 여부
  public boolean isReadBy(Long userId) {
    return readUserIds.contains(userId);
  }
}
