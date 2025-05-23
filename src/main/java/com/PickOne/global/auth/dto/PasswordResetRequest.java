package com.PickOne.global.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PasswordResetRequest(
        @NotBlank(message = "새 비밀번호는 필수입니다.")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message = "비밀번호는 최소 8자 이상이며, 대문자, 소문자, 숫자, 특수문자를 각각 1개 이상 포함해야 합니다."
        )
        String newPassword,

        @NotBlank(message = "비밀번호 확인은 필수입니다.")
        String confirmPassword
) {
    public boolean isPasswordMatching() {
        return newPassword.equals(confirmPassword);
    }
}