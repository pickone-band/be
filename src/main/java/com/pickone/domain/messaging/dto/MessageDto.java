package com.pickone.domain.messaging.dto;

import com.pickone.domain.messaging.model.document.MessageDocument;
import java.time.LocalDateTime;

public record MessageDto(Long roomId, Long senderId, String content, LocalDateTime sentAt) {

  public static MessageDto of(MessageDocument document) {
    return new MessageDto(
        document.getRoomId(),
        document.getSenderId(),
        document.getContent(),
        document.getSentAt()
    );
  }
}