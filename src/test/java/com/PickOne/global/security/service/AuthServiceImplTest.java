package com.PickOne.global.security.service;

import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
import com.PickOne.global.security.service.AuthServiceImpl;
import com.PickOne.global.security.service.JwtService;
import com.PickOne.global.security.service.PasswordEncoder;
import com.PickOne.domain.user.model.domain.User;
import com.PickOne.domain.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    @DisplayName("회원가입 성공 테스트")
    void signup_Success() {
        // given
        String email = "test@example.com";
        String password = "Password123!";
        String encodedPassword = "encoded-password";

        User expectedUser = mock(User.class);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(userRepository.save(any(User.class))).thenReturn(expectedUser);

        // when
        User result = authService.signup(email, password);

        // then
        verify(userRepository).findByEmail(email);
        verify(passwordEncoder).encode(password);
        verify(userRepository).save(any(User.class));
        assertThat(result).isEqualTo(expectedUser);
    }

    @Test
    @DisplayName("회원가입 실패 테스트 - 이메일 중복")
    void signup_DuplicateEmail() {
        // given
        String email = "test@example.com";
        String password = "Password123!";

        User existingUser = mock(User.class);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));

        // when & then
        assertThatThrownBy(() -> authService.signup(email, password))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_EMAIL);

        verify(userRepository).findByEmail(email);
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("로그인 성공 테스트")
    void login_Success() {
        // given
        String email = "test@example.com";
        String password = "Password123!";
        String encodedPassword = "encoded-password";

        User user = mock(User.class);
        when(user.getPasswordValue()).thenReturn(encodedPassword);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(true);

        // when
        User result = authService.login(email, password);

        // then
        verify(userRepository).findByEmail(email);
        verify(passwordEncoder).matches(password, encodedPassword);
        assertThat(result).isEqualTo(user);
    }

    @Test
    @DisplayName("로그인 실패 테스트 - 사용자 없음")
    void login_UserNotFound() {
        // given
        String email = "test@example.com";
        String password = "Password123!";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.login(email, password))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_PASSWORD);

        verify(userRepository).findByEmail(email);
        verify(passwordEncoder, never()).matches(any(), any());
    }

    @Test
    @DisplayName("로그인 실패 테스트 - 비밀번호 불일치")
    void login_InvalidPassword() {
        // given
        String email = "test@example.com";
        String password = "Password123!";
        String encodedPassword = "encoded-password";

        User user = mock(User.class);
        when(user.getPasswordValue()).thenReturn(encodedPassword);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, encodedPassword)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.login(email, password))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_PASSWORD);

        verify(userRepository).findByEmail(email);
        verify(passwordEncoder).matches(password, encodedPassword);
    }

    @Test
    @DisplayName("토큰 갱신 성공 테스트")
    void refreshToken_Success() {
        // given
        String refreshToken = "refresh-token";
        Long userId = 1L;

        User user = mock(User.class);
        when(jwtService.validateRefreshToken(refreshToken)).thenReturn(true);
        when(jwtService.getUserIdFromToken(refreshToken)).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // when
        User result = authService.refreshToken(refreshToken);

        // then
        verify(jwtService).validateRefreshToken(refreshToken);
        verify(jwtService).getUserIdFromToken(refreshToken);
        verify(userRepository).findById(userId);
        assertThat(result).isEqualTo(user);
    }

    @Test
    @DisplayName("토큰 갱신 실패 테스트 - 유효하지 않은 토큰")
    void refreshToken_InvalidToken() {
        // given
        String refreshToken = "refresh-token";

        when(jwtService.validateRefreshToken(refreshToken)).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.refreshToken(refreshToken))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_REFRESH_TOKEN);

        verify(jwtService).validateRefreshToken(refreshToken);
        verify(jwtService, never()).getUserIdFromToken(any());
        verify(userRepository, never()).findById(any());
    }

    @Test
    @DisplayName("토큰 갱신 실패 테스트 - 사용자 없음")
    void refreshToken_UserNotFound() {
        // given
        String refreshToken = "refresh-token";
        Long userId = 1L;

        when(jwtService.validateRefreshToken(refreshToken)).thenReturn(true);
        when(jwtService.getUserIdFromToken(refreshToken)).thenReturn(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.refreshToken(refreshToken))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_INFO_NOT_FOUND);

        verify(jwtService).validateRefreshToken(refreshToken);
        verify(jwtService).getUserIdFromToken(refreshToken);
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("로그아웃 성공 테스트")
    void logout_Success() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        String token = "access-token";

        when(jwtService.resolveToken(request)).thenReturn(token);
        doNothing().when(jwtService).blacklistToken(token);

        // when
        authService.logout(request);

        // then
        verify(jwtService).resolveToken(request);
        verify(jwtService).blacklistToken(token);
    }

    @Test
    @DisplayName("로그아웃 테스트 - 토큰 없음")
    void logout_NoToken() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(jwtService.resolveToken(request)).thenReturn(null);

        // when
        authService.logout(request);

        // then
        verify(jwtService).resolveToken(request);
        verify(jwtService, never()).blacklistToken(any());
    }

    @Test
    @DisplayName("액세스 토큰 생성 테스트")
    void generateAccessToken_Success() {
        // given
        Long userId = 1L;
        String email = "test@example.com";
        String expectedToken = "access-token";

        User user = mock(User.class);
        when(user.getId()).thenReturn(userId);
        when(user.getEmailValue()).thenReturn(email);

        when(jwtService.generateToken(any(), eq(email), anyLong())).thenReturn(expectedToken);
        when(jwtService.getAccessTokenExpiration()).thenReturn(3600000L);

        // when
        String token = authService.generateAccessToken(user);

        // then
        verify(jwtService).generateToken(any(), eq(email), anyLong());
        assertThat(token).isEqualTo(expectedToken);
    }

    @Test
    @DisplayName("리프레시 토큰 생성 테스트")
    void generateRefreshToken_Success() {
        // given
        Long userId = 1L;
        String email = "test@example.com";
        String expectedToken = "refresh-token";

        User user = mock(User.class);
        when(user.getId()).thenReturn(userId);
        when(user.getEmailValue()).thenReturn(email);

        when(jwtService.generateToken(any(), eq(email), anyLong())).thenReturn(expectedToken);
        when(jwtService.getRefreshTokenExpiration()).thenReturn(86400000L);

        // when
        String token = authService.generateRefreshToken(user);

        // then
        verify(jwtService).generateToken(any(), eq(email), anyLong());
        assertThat(token).isEqualTo(expectedToken);
    }
}