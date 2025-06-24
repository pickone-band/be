package com.pickone.global.verification.dto;

import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;

public record PasswordResetRequestDto(
    String token,
    String newPassword,
    String confirmPassword
) {

  public void validate() {
    if (!newPassword.equals(confirmPassword)) {
      throw new BusinessException(ErrorCode.INVALID_PASSWORD);
    }
  }
}