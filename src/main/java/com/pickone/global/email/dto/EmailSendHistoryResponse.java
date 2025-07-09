package com.pickone.global.email.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "이메일 발송 기록 응답")
public record EmailSendHistoryResponse(

    @Schema(description = "기록 ID", example = "1")
    Long id,

    @Schema(description = "수신자 이메일", example = "user@example.com")
    String toEmail,

    @Schema(description = "제목", example = "비밀번호 재설정 안내")
    String subject,

    @Schema(description = "성공 여부", example = "true")
    boolean success,

    @Schema(description = "발송 시각", example = "2024-07-01T12:00:00")
    LocalDateTime sentAt
) {}
