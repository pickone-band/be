package com.PickOne.global.verification.dto;

import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
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
        String confirmPassword,

        @NotBlank(message = "토큰은 필수입니다.")
        String token

) {
    public void validate() {
        if (!newPassword.equals(confirmPassword)) {
            throw new BusinessException(ErrorCode.PASSWORD_CONFIRM_NOT_MATCHED);
        }
    }
}
