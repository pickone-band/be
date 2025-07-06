package com.pickone.global.security.controller;

import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.global.exception.BaseResponse;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import com.pickone.global.exception.SuccessCode;
import com.pickone.global.security.dto.*;
import com.pickone.global.security.service.AuthService;
import com.pickone.global.security.service.EmailTokenService;
import com.pickone.global.security.service.PasswordResetTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "일반 로그인, 이메일 인증, 비밀번호 재설정 등 인증 관련 API")
public class AuthController {

  private final AuthService authService;
  private final EmailTokenService emailTokenService;
  private final UserJpaRepository userJpaRepository;
  private final PasswordResetTokenService passwordResetTokenService;

  @Operation(summary = "일반 로그인", description = "이메일/비밀번호 기반 로그인 후 액세스 토큰을 반환합니다.")
  @PostMapping("/login")
  public ResponseEntity<BaseResponse<LoginResponse>> login(
      @RequestBody LoginRequest request) {
    LoginResponse response = authService.login(request);
    return BaseResponse.success(response);
  }

  @Operation(summary = "로그아웃", description = "Refresh 토큰을 만료시켜 로그아웃 처리합니다.")
  @PostMapping("/logout")
  public ResponseEntity<BaseResponse<Void>> logout(
      @Parameter(description = "로그아웃할 Refresh Token") @RequestParam String refreshToken) {
    authService.logout(refreshToken);
    return BaseResponse.success(SuccessCode.OK);
  }

  @Operation(summary = "이메일 인증 처리", description = "이메일 인증 토큰을 검증하고 사용자 이메일을 인증합니다.")
  @GetMapping("/verify-email")
  public ResponseEntity<BaseResponse<Void>> verifyEmail(
      @Parameter(description = "이메일 인증 토큰") @RequestParam String token) {
    String email = emailTokenService.validateTokenAndGetEmail(token)
        .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_TOKEN));

    UserEntity user = userJpaRepository.findByProfileEmail(email)
        .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));

    user.verify();
    userJpaRepository.save(user);

    return BaseResponse.success(SuccessCode.OK);
  }

  @Operation(summary = "비밀번호 재설정 요청", description = "비밀번호 재설정 이메일을 발송합니다.")
  @PostMapping("/password-reset/request")
  public ResponseEntity<BaseResponse<Void>> requestPasswordReset(
      @Parameter(description = "비밀번호 재설정 대상 이메일") @RequestParam String email) {
    authService.sendPasswordResetEmail(email);
    return BaseResponse.success(SuccessCode.OK);
  }

  @Operation(summary = "비밀번호 재설정 토큰 검증", description = "비밀번호 재설정 페이지 접근 전 토큰 유효성 검사.")
  @GetMapping("/password-reset/verify")
  public ResponseEntity<BaseResponse<Void>> verifyResetToken(
      @Parameter(description = "비밀번호 재설정 토큰") @RequestParam String token) {
    passwordResetTokenService.validateTokenAndGetEmail(token);
    return BaseResponse.success(SuccessCode.OK);
  }

  @Operation(summary = "비밀번호 재설정 실행", description = "토큰과 새 비밀번호로 비밀번호를 재설정합니다.")
  @PostMapping("/password-reset")
  public ResponseEntity<BaseResponse<Void>> resetPasswordWithToken(
      @Parameter(description = "비밀번호 재설정 토큰") @RequestParam String token,
      @RequestBody PasswordResetRequest request) {
    authService.resetPasswordWithToken(token, request.newPassword());
    return BaseResponse.success(SuccessCode.UPDATED);
  }
}
