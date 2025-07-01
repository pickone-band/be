package com.pickone.global.email.dto;

import java.time.LocalDateTime;

public record EmailSendHistoryDto(
    Long id,
    String toEmail,
    String subject,
    boolean success,
    LocalDateTime sentAt
    // content, errorMessage 등 민감 정보는 제외/마스킹
) {}
