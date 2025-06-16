package com.PickOne.global.verification.controller;

import com.PickOne.domain.user.model.entity.UserEntity;
import com.PickOne.domain.user.service.UserService;
import com.PickOne.global.exception.BaseResponse;
import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
import com.PickOne.global.exception.SuccessCode;
import com.PickOne.global.verification.dto.*;
import com.PickOne.global.verification.service.EmailVerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/verification/email")
@RequiredArgsConstructor
@Tag(name = "Email Verification", description = "이메일 인증 및 비밀번호 재설정 API")
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;
    private final UserService userService;

    @PostMapping("/resend")
    @Operation(summary = "회원가입 이메일 재전송")
    public ResponseEntity<BaseResponse<Void>> resendVerificationEmail(
            @RequestBody @Valid EmailVerificationRequestDto request) {
        UserEntity user = userService.findByEmail(request.email());
        if (user.isVerified()) {
            throw new BusinessException(ErrorCode.ALREADY_VERIFIED);
        }
        emailVerificationService.sendVerificationEmail(user);
        return BaseResponse.success(SuccessCode.OK);
    }

    @PostMapping("/verify")
    @Operation(summary = "회원가입 이메일 인증")
    public ResponseEntity<BaseResponse<VerificationResponseDto>> verifyEmail(
            @RequestBody @Valid TokenVerificationRequestDto request) {
        boolean verified = emailVerificationService.verifyEmail(request.token());
        return BaseResponse.success(
                new VerificationResponseDto(verified, "이메일 인증이 완료되었습니다. 이제 로그인할 수 있습니다."));
    }

    @PostMapping("/reset-password/request")
    @Operation(summary = "비밀번호 재설정 이메일 전송")
    public ResponseEntity<BaseResponse<Void>> forgotPassword(
            @RequestBody @Valid EmailVerificationRequestDto request) {
        UserEntity user = userService.findByEmail(request.email());
        emailVerificationService.sendPasswordResetEmail(user);
        return BaseResponse.success(SuccessCode.OK);
    }

    @PostMapping("/reset-password/validate")
    @Operation(summary = "비밀번호 재설정 토큰 검증")
    public ResponseEntity<BaseResponse<VerificationResponseDto>> validateResetToken(
            @RequestBody @Valid TokenVerificationRequestDto request) {
        emailVerificationService.validatePasswordResetToken(request.token());
        return BaseResponse.success(
                new VerificationResponseDto(true, "유효한 토큰입니다. 비밀번호를 재설정할 수 있습니다."));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "비밀번호 재설정")
    public ResponseEntity<BaseResponse<Void>> resetPassword(
            @RequestBody @Valid PasswordResetRequestDto request) {
        request.validate();
        UserEntity user = emailVerificationService.validatePasswordResetToken(request.token());
        userService.updatePassword(user.getId(), request.newPassword());
        emailVerificationService.completePasswordReset(request.token());
        return BaseResponse.success(SuccessCode.OK);
    }
}
