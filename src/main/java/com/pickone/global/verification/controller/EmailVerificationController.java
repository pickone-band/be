package com.pickone.global.verification.controller;

import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.domain.user.service.UserService;
import com.pickone.global.exception.BaseResponse;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import com.pickone.global.exception.SuccessCode;
import com.pickone.global.verification.dto.*;
import com.pickone.global.verification.service.EmailVerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
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
    log.info("회원가입 이메일 재전송 요청: {}", request.email());
    UserEntity user = userService.findByEmail(request.email());
    if (user.isVerified()) {
      log.warn("이미 인증된 사용자: {}", request.email());
      throw new BusinessException(ErrorCode.ALREADY_VERIFIED);
    }
    emailVerificationService.sendVerificationEmail(user);
    log.info("이메일 재전송 완료: {}", request.email());
    return BaseResponse.success(SuccessCode.OK);
  }

  @PostMapping("/verify")
  @Operation(summary = "회원가입 이메일 인증")
  public ResponseEntity<BaseResponse<VerificationResponseDto>> verifyEmail(
      @RequestBody @Valid TokenVerificationRequestDto request) {
    log.info("이메일 인증 요청 수신: token={}", request.token());
    boolean verified = emailVerificationService.verifyEmail(request.token());
    log.info("이메일 인증 결과: verified={}", verified);
    return BaseResponse.success(
        new VerificationResponseDto(verified, "이메일 인증이 완료되었습니다. 이제 로그인할 수 있습니다."));
  }

  @PostMapping("/reset-password/request")
  @Operation(summary = "비밀번호 재설정 이메일 전송")
  public ResponseEntity<BaseResponse<Void>> forgotPassword(
      @RequestBody @Valid EmailVerificationRequestDto request) {
    log.info("비밀번호 재설정 이메일 요청: {}", request.email());
    UserEntity user = userService.findByEmail(request.email());
    emailVerificationService.sendPasswordResetEmail(user);
    log.info("비밀번호 재설정 이메일 발송 완료: {}", request.email());
    return BaseResponse.success(SuccessCode.OK);
  }

  @PostMapping("/reset-password/validate")
  @Operation(summary = "비밀번호 재설정 토큰 검증")
  public ResponseEntity<BaseResponse<VerificationResponseDto>> validateResetToken(
      @RequestBody @Valid TokenVerificationRequestDto request) {
    log.info("비밀번호 재설정 토큰 검증 요청: token={}", request.token());
    emailVerificationService.validatePasswordResetToken(request.token());
    log.info("비밀번호 재설정 토큰 유효함");
    return BaseResponse.success(
        new VerificationResponseDto(true, "유효한 토큰입니다. 비밀번호를 재설정할 수 있습니다."));
  }

  @PostMapping("/reset-password")
  @Operation(summary = "비밀번호 재설정")
  public ResponseEntity<BaseResponse<Void>> resetPassword(
      @RequestBody @Valid PasswordResetRequestDto request) {
    log.info("비밀번호 재설정 요청: token={}", request.token());
    request.validate();
    UserEntity user = emailVerificationService.validatePasswordResetToken(request.token());
    userService.updatePassword(user.getId(), request.newPassword());
    emailVerificationService.completePasswordReset(request.token());
    log.info("비밀번호 재설정 완료: userId={}", user.getId());
    return BaseResponse.success(SuccessCode.OK);
  }
}
