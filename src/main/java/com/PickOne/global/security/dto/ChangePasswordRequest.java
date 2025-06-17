package com.PickOne.global.security.dto;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(
        @NotBlank String currentPassword,
        @NotBlank String newPassword
) {
    public void validate() {
        if (currentPassword.equals(newPassword)) {
            throw new IllegalArgumentException("현재 비밀번호와 새 비밀번호가 동일합니다.");
        }
        if (newPassword.length() < 8) {
            throw new IllegalArgumentException("비밀번호는 최소 8자 이상이어야 합니다.");
        }
    }
}
