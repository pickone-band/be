package com.PickOne.security.controller;

import com.PickOne.global.exception.BaseResponse;
import com.PickOne.global.security.controller.AuthController;
import com.PickOne.global.security.dto.AuthResponse;
import com.PickOne.global.security.dto.LoginRequest;
import com.PickOne.global.security.dto.RefreshTokenRequest;
import com.PickOne.global.security.dto.SignupRequest;
import com.PickOne.global.security.service.AuthService;
import com.PickOne.domain.user.model.domain.User;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    @DisplayName("로그인 성공 테스트")
    void login_Success() {
        // given
        String email = "test@example.com";
        String password = "Password123!";
        LoginRequest request = new LoginRequest();
        request.setEmail(email);
        request.setPassword(password);

        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(1L);
        when(mockUser.getEmailValue()).thenReturn(email);

        String accessToken = "access-token";
        String refreshToken = "refresh-token";

        when(authService.login(email, password)).thenReturn(mockUser);
        when(authService.generateAccessToken(mockUser)).thenReturn(accessToken);
        when(authService.generateRefreshToken(mockUser)).thenReturn(refreshToken);

        // when
        ResponseEntity<BaseResponse<AuthResponse>> response = authController.login(request);

        // then
        verify(authService).login(email, password);
        verify(authService).generateAccessToken(mockUser);
        verify(authService).generateRefreshToken(mockUser);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getResult()).isNotNull();
        assertThat(response.getBody().getResult().getAccessToken()).isEqualTo(accessToken);
        assertThat(response.getBody().getResult().getRefreshToken()).isEqualTo(refreshToken);
        assertThat(response.getBody().getResult().getUserId()).isEqualTo(1L);
        assertThat(response.getBody().getResult().getEmail()).isEqualTo(email);
    }

    @Test
    @DisplayName("회원가입 성공 테스트")
    void signup_Success() {
        // given
        String email = "test@example.com";
        String password = "Password123!";
        SignupRequest request = new SignupRequest();
        request.setEmail(email);
        request.setPassword(password);

        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(1L);
        when(mockUser.getEmailValue()).thenReturn(email);

        String accessToken = "access-token";
        String refreshToken = "refresh-token";

        when(authService.signup(email, password)).thenReturn(mockUser);
        when(authService.generateAccessToken(mockUser)).thenReturn(accessToken);
        when(authService.generateRefreshToken(mockUser)).thenReturn(refreshToken);

        // when
        ResponseEntity<BaseResponse<AuthResponse>> response = authController.signup(request);

        // then
        verify(authService).signup(email, password);
        verify(authService).generateAccessToken(mockUser);
        verify(authService).generateRefreshToken(mockUser);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getResult()).isNotNull();
        assertThat(response.getBody().getResult().getAccessToken()).isEqualTo(accessToken);
        assertThat(response.getBody().getResult().getRefreshToken()).isEqualTo(refreshToken);
        assertThat(response.getBody().getResult().getUserId()).isEqualTo(1L);
        assertThat(response.getBody().getResult().getEmail()).isEqualTo(email);
    }

    @Test
    @DisplayName("토큰 갱신 성공 테스트")
    void refreshToken_Success() {
        // given
        String refreshToken = "refresh-token";
        RefreshTokenRequest request = new RefreshTokenRequest();
        request.setRefreshToken(refreshToken);

        User mockUser = mock(User.class);
        when(mockUser.getId()).thenReturn(1L);
        when(mockUser.getEmailValue()).thenReturn("test@example.com");

        String newAccessToken = "new-access-token";

        when(authService.refreshToken(refreshToken)).thenReturn(mockUser);
        when(authService.generateAccessToken(mockUser)).thenReturn(newAccessToken);

        // when
        ResponseEntity<BaseResponse<AuthResponse>> response = authController.refreshToken(request);

        // then
        verify(authService).refreshToken(refreshToken);
        verify(authService).generateAccessToken(mockUser);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getResult()).isNotNull();
        assertThat(response.getBody().getResult().getAccessToken()).isEqualTo(newAccessToken);
        assertThat(response.getBody().getResult().getRefreshToken()).isEqualTo(refreshToken);
        assertThat(response.getBody().getResult().getUserId()).isEqualTo(1L);
        assertThat(response.getBody().getResult().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("로그아웃 성공 테스트")
    void logout_Success() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        doNothing().when(authService).logout(request);

        // when
        ResponseEntity<BaseResponse<Void>> response = authController.logout(request);

        // then
        verify(authService).logout(request);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getIsSuccess()).isTrue();
    }
}