package com.PickOne.global.auth.controller;

import com.PickOne.domain.user.model.domain.User;
import com.PickOne.domain.user.service.UserService;
import com.PickOne.global.auth.dto.PasswordResetRequest;
import com.PickOne.global.auth.dto.VerificationResponse;
import com.PickOne.global.auth.service.EmailVerificationService;
import com.PickOne.global.exception.BaseResponse;
import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
import com.PickOne.global.exception.SuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/verification")
@RequiredArgsConstructor
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;
    private final UserService userService;

    /**
     * 이메일 인증 토큰 재발송
     */
    @PostMapping("/resend")
    public ResponseEntity<BaseResponse<Void>> resendVerificationEmail(@RequestParam String email) {
        User user = userService.findByEmail(email);

        // 이미 인증된 사용자인지 확인
        if (user.isVerified()) {
            throw new BusinessException(ErrorCode.ALREADY_VERIFIED);
        }

        emailVerificationService.sendVerificationEmail(user);
        return BaseResponse.success(SuccessCode.OK);
    }

    /**
     * 이메일 인증 토큰 검증
     */
    @GetMapping("/verify")
    public ResponseEntity<BaseResponse<VerificationResponse>> verifyEmail(@RequestParam String token) {
        boolean verified = emailVerificationService.verifyEmail(token);

        VerificationResponse response = new VerificationResponse(
                verified,
                "이메일 인증이 완료되었습니다. 이제 로그인할 수 있습니다."
        );

        return BaseResponse.success(response);
    }

    /**
     * 비밀번호 재설정 요청
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<BaseResponse<Void>> forgotPassword(@RequestParam String email) {
        User user = userService.findByEmail(email);
        emailVerificationService.sendPasswordResetEmail(user);
        return BaseResponse.success(SuccessCode.OK);
    }

    /**
     * 비밀번호 재설정 토큰 검증
     */
    @GetMapping("/reset-password")
    public ResponseEntity<BaseResponse<VerificationResponse>> validateResetToken(@RequestParam String token) {
        User user = emailVerificationService.validatePasswordResetToken(token);

        VerificationResponse response = new VerificationResponse(
                true,
                "유효한 토큰입니다. 비밀번호를 재설정할 수 있습니다."
        );

        return BaseResponse.success(response);
    }

    /**
     * 비밀번호 재설정 실행
     */
    @PostMapping("/reset-password")
    public ResponseEntity<BaseResponse<Void>> resetPassword(
            @RequestParam String token,
            @Valid @RequestBody PasswordResetRequest request) {

        // 비밀번호 일치 확인
        if (!request.isPasswordMatching()) {
            throw new BusinessException(ErrorCode.PASSWORD_MISMATCH);
        }

        User user = emailVerificationService.validatePasswordResetToken(token);

        userService.updatePassword(user.getId(), request.newPassword());

        emailVerificationService.completePasswordReset(token);

        return BaseResponse.success(SuccessCode.OK);
    }
}