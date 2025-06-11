package com.PickOne.global.security.controller;

import com.PickOne.global.exception.BaseResponse;
import com.PickOne.global.exception.SuccessCode;
import com.PickOne.global.security.dto.*;
import com.PickOne.global.security.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 회원가입
     */
    @PostMapping("/signup")
    public ResponseEntity<BaseResponse<AuthResponseDto>> signup(@RequestBody @Valid SignupRequestDto request) {
        AuthResult result = authService.signup(request);
        return BaseResponse.success(SuccessCode.CREATED, AuthResponseDto.of(result));
    }

    /**
     * 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<BaseResponse<AuthResponseDto>> login(@RequestBody @Valid LoginRequest request) {
        AuthResult result = authService.login(request);
        return BaseResponse.success(AuthResponseDto.of(result));
    }

    /**
     * 토큰 재발급
     */
    @PostMapping("/refresh")
    public ResponseEntity<BaseResponse<AuthResponseDto>> refresh(@RequestBody @Valid RefreshTokenRequest request) {
        AuthResult result = authService.refresh(request.refreshToken());
        return BaseResponse.success(AuthResponseDto.of(result));
    }

    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    public ResponseEntity<BaseResponse<Void>> logout(@RequestHeader("Authorization") String accessToken) {
        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7);
        }
        authService.logout(accessToken);
        return BaseResponse.success();
    }
}
