package com.pickone.global.security.controller;

import com.pickone.global.exception.BaseResponse;
import com.pickone.global.exception.SuccessCode;
import com.pickone.global.security.dto.*;
import com.pickone.global.security.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/login")
  public ResponseEntity<BaseResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
    LoginResponse response = authService.login(request);
    return BaseResponse.success(response);
  }

  /**
   * 로그아웃
   */
  @PostMapping("/logout")
  public ResponseEntity<BaseResponse<Void>> logout(@RequestParam String refreshToken) {
    authService.logout(refreshToken);
    return BaseResponse.success(SuccessCode.OK);
  }

  /**
   * 비밀번호 재설정
   */
  @PostMapping("/password-reset")
  public ResponseEntity<BaseResponse<Void>> resetPassword(@RequestBody PasswordResetRequest request) {
    authService.resetPassword(request);
    return BaseResponse.success(SuccessCode.UPDATED);
  }
}
