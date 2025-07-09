package com.pickone.global.security.dto;

import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "비밀번호 재설정 요청")
public record PasswordResetRequest(
    @NotBlank @Schema(description = "새 비밀번호", example = "NewPass123!") String newPassword
) {
  public void validate() {
    if (newPassword.length() < 8) {
      throw new BusinessException(ErrorCode.INVALID_PASSWORD);
    }
  }
}