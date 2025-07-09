package com.pickone.global.security.controller;

import com.pickone.domain.user.entity.User;
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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "인증 관련 API")
@Slf4j
public class AuthController {

  private final AuthService authService;
  private final EmailTokenService emailTokenService;
  private final UserJpaRepository userRepository;
  private final PasswordResetTokenService passwordResetTokenService;

  @Operation(summary = "로그인", description = "이메일/비밀번호로 로그인하고 토큰을 반환합니다.")
  @PostMapping("/login")
  public ResponseEntity<BaseResponse<LoginResponse>> login(
      @RequestBody @Valid LoginRequest request
  ) {
    log.info("[AuthController] login 요청: email={}", request.email());
    LoginResponse resp = authService.login(request);
    return BaseResponse.success(resp);
  }

  @Operation(summary = "로그아웃", description = "리프레시 토큰을 만료시켜 로그아웃 처리합니다.")
  @PostMapping("/logout")
  public ResponseEntity<BaseResponse<Void>> logout(
      @Parameter(description = "리프레시 토큰", required = true) @RequestParam String refreshToken
  ) {
    log.info("[AuthController] logout 요청: refreshToken={}", mask(refreshToken));
    authService.logout(refreshToken);
    return BaseResponse.success();
  }

  @Operation(summary = "이메일 인증", description = "발급된 이메일 인증 토큰으로 사용자 인증을 수행합니다.")
  @GetMapping("/verify-email")
  public ResponseEntity<BaseResponse<Void>> verifyEmail(
      @Parameter(description = "이메일 인증 토큰", required = true) @RequestParam String token
  ) {
    log.info("[AuthController] verifyEmail 요청: token={}", mask(token));
    String email = emailTokenService.validateTokenAndGetEmail(token)
        .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_TOKEN));

    User user = userRepository.findByProfileEmail(email)
        .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));
    user.verify();
    userRepository.save(user);

    return BaseResponse.success();
  }

  @Operation(summary = "비밀번호 재설정 요청", description = "비밀번호 재설정 이메일을 발송합니다.")
  @PostMapping("/password-reset/request")
  public ResponseEntity<BaseResponse<Void>> requestReset(
      @Parameter(description = "대상 이메일", required = true) @RequestParam String email
  ) {
    log.info("[AuthController] requestReset 요청: email={}", email);
    authService.sendPasswordResetEmail(email);
    return BaseResponse.success();
  }

  @Operation(summary = "비밀번호 재설정 토큰 검증", description = "입력한 토큰의 유효성을 확인합니다.")
  @GetMapping("/password-reset/verify")
  public ResponseEntity<BaseResponse<Void>> verifyResetToken(
      @Parameter(description = "비밀번호 재설정 토큰", required = true) @RequestParam String token
  ) {
    log.info("[AuthController] verifyResetToken 요청: token={}", mask(token));
    passwordResetTokenService.validateTokenAndGetEmail(token);
    return BaseResponse.success();
  }

  @Operation(summary = "비밀번호 재설정 실행", description = "새 비밀번호로 비밀번호를 재설정합니다.")
  @PostMapping("/password-reset")
  public ResponseEntity<BaseResponse<Void>> resetPassword(
      @Parameter(description = "비밀번호 재설정 토큰", required = true) @RequestParam String token,
      @RequestBody @Valid PasswordResetRequest request
  ) {
    log.info("[AuthController] resetPassword 요청: token={}", mask(token));
    authService.resetPasswordWithToken(token, request.newPassword());
    return BaseResponse.success(SuccessCode.UPDATED);
  }

  private String mask(String s) {
    return (s == null || s.length() < 8) ? "***" : s.substring(0,4) + "...";
  }
}
