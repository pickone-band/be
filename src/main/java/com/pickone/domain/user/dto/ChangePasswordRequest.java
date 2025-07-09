package com.pickone.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "비밀번호 변경 요청")
public record ChangePasswordRequest(

    @Schema(description = "새 비밀번호", example = "mySecurePassword123!")
    String newPassword

) {}