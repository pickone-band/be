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
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;
  private final EmailTokenService emailTokenService;
  private final UserJpaRepository userJpaRepository;
  private final PasswordResetTokenService passwordResetTokenService;

  @PostMapping("/login")
  public ResponseEntity<BaseResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
    LoginResponse response = authService.login(request);
    return BaseResponse.success(response);
  }

  @PostMapping("/logout")
  public ResponseEntity<BaseResponse<Void>> logout(@RequestParam String refreshToken) {
    authService.logout(refreshToken);
    return BaseResponse.success(SuccessCode.OK);
  }

  /**
   * 이메일 인증 토큰으로 이메일 인증 처리
   */
  @GetMapping("/verify-email")
  public ResponseEntity<BaseResponse<Void>> verifyEmail(@RequestParam String token) {
    String email = emailTokenService.validateTokenAndGetEmail(token)
        .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_TOKEN));

    UserEntity user = userJpaRepository.findByProfileEmail(email)
        .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));

    user.verify();
    userJpaRepository.save(user);

    return BaseResponse.success(SuccessCode.OK);
  }

  /**
   * 비밀번호 재설정 요청 (이메일 발송)
   */
  @PostMapping("/password-reset/request")
  public ResponseEntity<BaseResponse<Void>> requestPasswordReset(@RequestParam String email) {
    authService.sendPasswordResetEmail(email);
    return BaseResponse.success(SuccessCode.OK);
  }

  /**
   * 비밀번호 재설정 페이지 접근을 위한 토큰 검증
   */
  @GetMapping("/password-reset/verify")
  public ResponseEntity<BaseResponse<Void>> verifyResetToken(@RequestParam String token) {
    passwordResetTokenService.validateTokenAndGetEmail(token);
    return BaseResponse.success(SuccessCode.OK);
  }

  /**
   * 비밀번호 재설정 (토큰과 새 비밀번호를 받아서 처리)
   */
  @PostMapping("/password-reset")
  public ResponseEntity<BaseResponse<Void>> resetPasswordWithToken(
      @RequestParam String token,
      @RequestBody PasswordResetRequest request) {
    authService.resetPasswordWithToken(token, request.newPassword());
    return BaseResponse.success(SuccessCode.UPDATED);
  }
}
