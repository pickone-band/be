package com.PickOne.global.security.service;

import com.PickOne.domain.user.model.domain.Email;
import com.PickOne.domain.user.model.domain.Password;
import com.PickOne.domain.user.model.domain.User;
import com.PickOne.domain.user.repository.UserRepository;
import com.PickOne.global.security.config.PasswordEncoder;
import com.PickOne.global.security.dto.LoginRequest;
import com.PickOne.global.security.dto.SignupRequest;
import com.PickOne.global.security.repository.RefreshTokenRepository;
import com.PickOne.global.security.repository.TokenBlacklistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private TokenBlacklistRepository tokenBlacklistRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("회원가입 성공")
    void signup_success() {
        SignupRequest request = new SignupRequest("user@example.com", "password123", "닉네임");

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encoded123");
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtService.generateAccessToken(any())).thenReturn("access-token");
        when(jwtService.generateRefreshToken(any())).thenReturn("refresh-token");

        var result = authService.signup(request);

        assertThat(result.accessToken()).isNotBlank();
        assertThat(result.refreshToken()).isNotBlank();
        assertThat(result.user().getEmail().getValue()).isEqualTo(request.email());
    }

    @Test
    @DisplayName("로그인 성공")
    void login_success() {
        Email email = Email.of("login@example.com");
        Password password = Password.ofEncoded("encoded123");
        User user = new User(1L, email, password, "유저", true);

        when(userRepository.findByEmail(email.getValue())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("rawpass", "encoded123")).thenReturn(true);
        when(jwtService.generateAccessToken(any())).thenReturn("access-token");
        when(jwtService.generateRefreshToken(any())).thenReturn("refresh-token");

        LoginRequest request = new LoginRequest(email.getValue(), "rawpass");

        var result = authService.login(request);

        assertThat(result.accessToken()).isNotNull();
        assertThat(result.refreshToken()).isNotNull();
        assertThat(result.user().getEmail().getValue()).isEqualTo(email.getValue());
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void login_wrong_password() {
        Email email = Email.of("fail@example.com");
        Password password = Password.ofEncoded("encoded123");
        User user = new User(2L, email, password, "닉", true);

        when(userRepository.findByEmail(email.getValue())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded123")).thenReturn(false);

        LoginRequest request = new LoginRequest(email.getValue(), "wrong");

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("비밀번호가 일치하지 않습니다");
    }

    @Test
    @DisplayName("리프레시 토큰 성공")
    void refresh_success() {
        String refreshToken = "refresh.token.value";
        String email = "refresh@example.com";
        User user = new User(3L, Email.of(email), Password.ofEncoded("pw"), "닉", true);

        when(jwtService.validateRefreshToken(refreshToken)).thenReturn(true);
        when(jwtService.extractUsername(refreshToken)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken(any())).thenReturn("access-token");
        when(jwtService.generateRefreshToken(any())).thenReturn("refresh-token");

        var result = authService.refresh(refreshToken);

        assertThat(result.accessToken()).isNotNull();
        assertThat(result.refreshToken()).isNotNull();
    }

    @Test
    @DisplayName("로그아웃 - 블랙리스트 추가")
    void logout_success() {
        String accessToken = "access.token.value";
        doNothing().when(jwtService).blacklistToken(accessToken);

        authService.logout(accessToken);

        verify(jwtService, times(1)).blacklistToken(accessToken);
    }
}
