package com.pickone.global.email.mapper;

import com.pickone.global.email.dto.EmailSendHistoryResponse;
import com.pickone.global.email.entity.EmailSendHistory;
import org.springframework.stereotype.Component;

@Component
public class EmailSendHistoryMapper {

  public EmailSendHistoryResponse toDto(EmailSendHistory entity) {
    if (entity == null) return null;

    return new EmailSendHistoryResponse(
        entity.getId(),
        entity.getToEmail(),
        entity.getSubject(),
        entity.isSuccess(),
        entity.getSentAt()
    );
  }
}
