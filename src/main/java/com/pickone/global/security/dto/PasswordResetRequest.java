package com.pickone.global.security.dto;

import jakarta.validation.constraints.NotBlank;

public record PasswordResetRequest(
    @NotBlank String newPassword
) {
  public void validate() {
    if (newPassword.length() < 8) {
      throw new IllegalArgumentException("비밀번호는 최소 8자 이상이어야 합니다.");
    }
  }
}
