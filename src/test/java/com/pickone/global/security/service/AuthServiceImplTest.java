package com.pickone.global.security.service;

import com.pickone.domain.user.entity.User;
import com.pickone.domain.user.model.domain.Gender;
import com.pickone.domain.user.model.vo.UserAuthInfo;
import com.pickone.domain.user.model.vo.UserProfile;
import com.pickone.domain.user.model.vo.UserStatus;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.global.common.enums.Mbti;
import com.pickone.global.email.service.EmailSendService;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import com.pickone.global.security.dto.LoginRequest;
import com.pickone.global.security.dto.LoginResponse;
import com.pickone.global.security.token.TokenProvider;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static com.pickone.domain.user.model.domain.Gender.MALE;
import static com.pickone.global.common.enums.Mbti.INTJ;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {

  private AuthServiceImpl authService;

  private UserJpaRepository userRepository;
  private PasswordEncoder passwordEncoder;
  private TokenProvider tokenProvider;
  private EmailTokenService emailTokenService;
  private EmailSendService emailSendService;

  @BeforeEach
  void setUp() {
    userRepository = mock(UserJpaRepository.class);
    passwordEncoder = mock(PasswordEncoder.class);
    tokenProvider = mock(TokenProvider.class);
    emailTokenService = mock(EmailTokenService.class);
    emailSendService = mock(EmailSendService.class);

    authService = new AuthServiceImpl(userRepository, passwordEncoder, tokenProvider, emailTokenService, emailSendService);
  }

  @Test
  void login_success() {
    // given
    String email = "test@example.com";
    String password = "password123";
    LoginRequest request = new LoginRequest(email, password);

    User user = User.create(
        email, "pw", "nick",
        Gender.MALE,
        LocalDate.now(),
        Mbti.INTP,
        List.of(), // ✅ null 대신 빈 리스트
        null,
        null
    );

    user.verify();

    when(userRepository.findByProfileEmail(email)).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(password, user.getAuthInfo().getPassword())).thenReturn(true);
    when(tokenProvider.generateAccessToken(any())).thenReturn("access-token");
    when(tokenProvider.generateRefreshToken(any())).thenReturn("refresh-token");

    // when
    LoginResponse response = authService.login(request);

    // then
    assertNotNull(response);
    assertEquals(user.getId(), response.userId());
    assertEquals("access-token", response.accessToken());
    assertEquals("refresh-token", response.refreshToken());
  }

  @Test
  void login_userNotFound_shouldThrow() {
    LoginRequest request = new LoginRequest("none@example.com", "pw");

    when(userRepository.findByProfileEmail(request.email())).thenReturn(Optional.empty());

    BusinessException ex = assertThrows(BusinessException.class, () -> authService.login(request));
    assertEquals(ErrorCode.LOGIN_USER_NOT_FOUND, ex.getErrorCode());
  }

  @Test
  void login_invalidPassword_shouldThrow() {
    String email = "user@example.com";
    LoginRequest request = new LoginRequest(email, "wrong");

    User user = User.create(
        email, "pw", "nick",
        Gender.MALE,
        LocalDate.now(),
        Mbti.INTP,
        List.of(), // ✅ null 대신 빈 리스트
        null,
        null
    );
    user.verify();

    when(userRepository.findByProfileEmail(email)).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(request.password(), user.getAuthInfo().getPassword())).thenReturn(false);

    BusinessException ex = assertThrows(BusinessException.class, () -> authService.login(request));
    assertEquals(ErrorCode.INVALID_PASSWORD_CREDENTIAL, ex.getErrorCode());
  }

  @Test
  void login_unverifiedUser_shouldThrow() {
    String email = "user@example.com";
    LoginRequest request = new LoginRequest(email, "pw");

    User user = User.create(
        email, "pw", "nick",
        Gender.MALE,
        LocalDate.now(),
        Mbti.INTP,
        List.of(), // ✅ null 대신 빈 리스트
        null,
        null
    );
    // not verified

    when(userRepository.findByProfileEmail(email)).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(request.password(), user.getAuthInfo().getPassword())).thenReturn(true);

    BusinessException ex = assertThrows(BusinessException.class, () -> authService.login(request));
    assertEquals(ErrorCode.EMAIL_NOT_VERIFIED, ex.getErrorCode());
  }

  @Test
  void logout_nullToken_shouldThrow() {
    BusinessException ex = assertThrows(BusinessException.class, () -> authService.logout(null));
    assertEquals(ErrorCode.TOKEN_NOT_FOUND, ex.getErrorCode());
  }

  @Test
  void logout_validToken_shouldBlacklist() {
    String token = "refresh-token";
    authService.logout(token);
    verify(tokenProvider).blacklistToken(token);
  }

  @Test
  void sendPasswordResetEmail_success() {
    String email = "reset@example.com";
    User user = mock(User.class);

    when(userRepository.findByProfileEmail(email)).thenReturn(Optional.of(user));
    when(emailTokenService.createAndSaveToken(email)).thenReturn("reset-token");

    authService.sendPasswordResetEmail(email);

    verify(emailSendService).sendPasswordResetEmail(email, "reset-token");
  }

  @Test
  void sendPasswordResetEmail_userNotFound_shouldThrow() {
    when(userRepository.findByProfileEmail("nope@example.com")).thenReturn(Optional.empty());

    assertThrows(BusinessException.class, () ->
        authService.sendPasswordResetEmail("nope@example.com"));
  }

  @Test
  void resetPasswordWithToken_success() {
    String token = "reset-token";
    String email = "user@example.com";
    String newPassword = "newPass";

    User user = User.create(
        email, "pw", "nick",
        Gender.MALE,
        LocalDate.now(),
        Mbti.INTP,
        List.of(), // ✅ null 대신 빈 리스트
        null,
        null
    );

    when(emailTokenService.validateTokenAndGetEmail(token)).thenReturn(Optional.of(email));
    when(userRepository.findByProfileEmail(email)).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(newPassword, user.getAuthInfo().getPassword())).thenReturn(false);
    when(passwordEncoder.encode(newPassword)).thenReturn("hashed-new");

    authService.resetPasswordWithToken(token, newPassword);

    verify(userRepository).save(user);
    verify(emailTokenService).deleteToken(token);
  }

  @Test
  void resetPasswordWithToken_sameAsOldPassword_shouldThrow() {
    String token = "t";
    String email = "user@example.com";
    String newPassword = "same";

    User user = User.create(
        email, "pw", "nick",
        Gender.MALE,
        LocalDate.now(),
        Mbti.INTP,
        List.of(), // ✅ null 대신 빈 리스트
        null,
        null
    );

    when(emailTokenService.validateTokenAndGetEmail(token)).thenReturn(Optional.of(email));
    when(userRepository.findByProfileEmail(email)).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(newPassword, user.getAuthInfo().getPassword())).thenReturn(true);

    BusinessException ex = assertThrows(BusinessException.class,
        () -> authService.resetPasswordWithToken(token, newPassword));

    assertEquals(ErrorCode.SAME_AS_OLD_PASSWORD, ex.getErrorCode());
  }

  @Test
  void resetPasswordWithToken_invalidToken_shouldThrow() {
    when(emailTokenService.validateTokenAndGetEmail("invalid")).thenReturn(Optional.empty());

    assertThrows(BusinessException.class,
        () -> authService.resetPasswordWithToken("invalid", "pw"));
  }
}
