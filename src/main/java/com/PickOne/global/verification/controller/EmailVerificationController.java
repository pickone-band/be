package com.PickOne.global.verification.controller;

import com.PickOne.domain.user.model.domain.User;
import com.PickOne.domain.user.service.UserService;
import com.PickOne.global.exception.BaseResponse;
import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
import com.PickOne.global.exception.SuccessCode;
import com.PickOne.global.verification.dto.EmailRequest;
import com.PickOne.global.verification.dto.PasswordResetRequest;
import com.PickOne.global.verification.dto.TokenRequest;
import com.PickOne.global.verification.dto.VerificationResponse;
import com.PickOne.global.verification.service.EmailVerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/verification/email")
@RequiredArgsConstructor
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;
    private final UserService userService;

    // 인증 메일 재전송
    @PostMapping("/resend")
    public ResponseEntity<BaseResponse<Void>> resendVerificationEmail(
            @RequestBody @Valid EmailRequest request) {
        User user = userService.findByEmail(request.email());
        if (user.isVerified()) {
            throw new BusinessException(ErrorCode.ALREADY_VERIFIED);
        }
        emailVerificationService.sendVerificationEmail(user);
        return BaseResponse.success(SuccessCode.OK);
    }

    // 인증 토큰 검증
    @PostMapping("/verify")
    public ResponseEntity<BaseResponse<VerificationResponse>> verifyEmail(
            @RequestBody @Valid TokenRequest request) {
        boolean verified = emailVerificationService.verifyEmail(request.token());
        VerificationResponse response =
                new VerificationResponse(verified, "이메일 인증이 완료되었습니다. 이제 로그인할 수 있습니다.");
        return BaseResponse.success(response);
    }

    // 비밀번호 재설정 요청
    @PostMapping("/reset-password/request")
    public ResponseEntity<BaseResponse<Void>> forgotPassword(
            @RequestBody @Valid EmailRequest request) {
        User user = userService.findByEmail(request.email());
        emailVerificationService.sendPasswordResetEmail(user);
        return BaseResponse.success(SuccessCode.OK);
    }

    // 비밀번호 재설정 토큰 검증
    @PostMapping("/reset-password/validate")
    public ResponseEntity<BaseResponse<VerificationResponse>> validateResetToken(
            @RequestBody @Valid TokenRequest request) {
        emailVerificationService.validatePasswordResetToken(request.token());
        VerificationResponse response = new VerificationResponse(true, "유효한 토큰입니다. 비밀번호를 재설정할 수 있습니다.");
        return BaseResponse.success(response);
    }

    // 비밀번호 재설정 실행
    @PostMapping("/reset-password")
    public ResponseEntity<BaseResponse<Void>> resetPassword(
            @RequestBody @Valid PasswordResetRequest request) {
        request.validate();
        User user = emailVerificationService.validatePasswordResetToken(request.token());
        userService.updatePassword(user.getId(), request.newPassword());
        emailVerificationService.completePasswordReset(request.token());
        return BaseResponse.success(SuccessCode.OK);
    }
}
