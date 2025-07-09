package com.pickone.global.email.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "이메일 전송 요청")
public record EmailSendRequest(

    @NotBlank
    @Email
    @Schema(description = "수신자 이메일", example = "user@example.com")
    String to,

    @NotBlank
    @Schema(description = "이메일 제목", example = "비밀번호 재설정 안내")
    String subject,

    @NotBlank
    @Schema(description = "이메일 본문", example = "아래 링크를 통해 비밀번호를 재설정하세요.")
    String content
) {}