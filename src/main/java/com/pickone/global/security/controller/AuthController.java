package com.pickone.global.security.controller;

import com.pickone.domain.user.dto.SignupRequestDto;
import com.pickone.global.exception.BaseResponse;
import com.pickone.global.exception.SuccessCode;
import com.pickone.global.security.dto.*;
import com.pickone.global.security.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/signup")
  public ResponseEntity<BaseResponse<AuthResponseDto>> signup(
      @RequestBody @Valid SignupRequestDto request) {
    log.info("회원가입 요청: email={}, nickname={}", request.email(), request.nickname());
    AuthResult result = authService.signup(request);
    log.info("회원가입 완료: email={}", request.email());
    return BaseResponse.success(SuccessCode.CREATED, AuthResponseDto.of(result));
  }

  @PostMapping("/login")
  public ResponseEntity<BaseResponse<AuthResponseDto>> login(
      @RequestBody @Valid LoginRequest request) {
    log.info("로그인 요청: email={}", request.email());
    AuthResult result = authService.login(request);
    log.info("로그인 성공: email={}", request.email());
    return BaseResponse.success(AuthResponseDto.of(result));
  }

  @PostMapping("/refresh")
  public ResponseEntity<BaseResponse<AuthResponseDto>> refresh(
      @RequestBody @Valid RefreshTokenRequest request) {
    log.info("토큰 재발급 요청");
    AuthResult result = authService.refresh(request.refreshToken());
    log.info("토큰 재발급 완료");
    return BaseResponse.success(AuthResponseDto.of(result));
  }

  @PostMapping("/logout")
  public ResponseEntity<BaseResponse<Void>> logout(
      @RequestHeader("Authorization") String accessToken) {
    log.info("로그아웃 요청");
    if (accessToken != null && accessToken.startsWith("Bearer ")) {
      accessToken = accessToken.substring(7);
    }
    authService.logout(accessToken);
    log.info("로그아웃 완료");
    return BaseResponse.success();
  }

  @Operation(summary = "비밀번호 변경", description = "기존 비밀번호를 확인하고 새 비밀번호로 변경합니다.")
  @PostMapping("/change-password")
  public ResponseEntity<BaseResponse<Void>> changePassword(
      @RequestHeader("Authorization") String accessToken,
      @RequestBody @Valid ChangePasswordRequest request
  ) {
    log.info("비밀번호 변경 요청");
    if (accessToken != null && accessToken.startsWith("Bearer ")) {
      accessToken = accessToken.substring(7);
    }
    request.validate();
    authService.changePassword(accessToken, request);
    log.info("비밀번호 변경 완료");
    return BaseResponse.success(SuccessCode.UPDATED);
  }
}
