package com.pickone.global.email.model.mapper;

import com.pickone.global.email.model.entity.EmailSendHistoryEntity;
import com.pickone.global.email.dto.EmailSendHistoryDto;

public class EmailSendHistoryMapper {
  public static EmailSendHistoryDto toDto(EmailSendHistoryEntity h) {
    return new EmailSendHistoryDto(
        h.getId(),
        maskEmail(h.getToEmail()),
        h.getSubject(),
        h.isSuccess(),
        h.getSentAt()
    );
  }

  // 간단한 이메일 마스킹
  private static String maskEmail(String email) {
    if (email == null) return null;
    int idx = email.indexOf("@");
    if (idx <= 2) return "***" + email.substring(idx);
    return email.substring(0, 2) + "***" + email.substring(idx);
  }
}
