package com.PickOne.security.controller;

import com.PickOne.global.exception.BaseResponse;
import com.PickOne.global.exception.SuccessCode;
import com.PickOne.security.dto.AuthResponse;
import com.PickOne.security.dto.LoginRequest;
import com.PickOne.security.dto.RefreshTokenRequest;
import com.PickOne.security.dto.SignupRequest;
import com.PickOne.security.service.AuthService;
import com.PickOne.user.model.domain.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        // 서비스 호출 - 도메인 객체 반환
        User user = authService.login(request.getEmail(), request.getPassword());

        // 토큰 생성
        String accessToken = authService.generateAccessToken(user);
        String refreshToken = authService.generateRefreshToken(user);

        // DTO로 변환하여 응답
        AuthResponse response = AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .email(user.getEmailValue())
                .build();

        return BaseResponse.success(SuccessCode.OK, response);
    }

    @PostMapping("/signup")
    public ResponseEntity<BaseResponse<AuthResponse>> signup(@Valid @RequestBody SignupRequest request) {
        // 서비스 호출 - 도메인 객체 반환
        User user = authService.signup(request.getEmail(), request.getPassword());

        // 토큰 생성
        String accessToken = authService.generateAccessToken(user);
        String refreshToken = authService.generateRefreshToken(user);

        // DTO로 변환하여 응답
        AuthResponse response = AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .email(user.getEmailValue())
                .build();

        return BaseResponse.success(SuccessCode.CREATED, response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<BaseResponse<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        // 서비스 호출 - 도메인 객체 반환
        User user = authService.refreshToken(request.getRefreshToken());

        // 새로운 액세스 토큰 생성
        String accessToken = authService.generateAccessToken(user);

        // DTO로 변환하여 응답
        AuthResponse response = AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(request.getRefreshToken())
                .userId(user.getId())
                .email(user.getEmailValue())
                .build();

        return BaseResponse.success(SuccessCode.OK, response);
    }

    @PostMapping("/logout")
    public ResponseEntity<BaseResponse<Void>> logout(HttpServletRequest request) {
        authService.logout(request);
        return BaseResponse.success();
    }
}