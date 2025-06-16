package com.PickOne.global.verification.dto;

import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;

public record PasswordResetRequestDto(
        String token,
        String newPassword,
        String confirmPassword
) {
    public void validate() {
        if (!newPassword.equals(confirmPassword)) {
            throw new BusinessException(ErrorCode.PASSWORD_MISMATCH);
        }
    }
}