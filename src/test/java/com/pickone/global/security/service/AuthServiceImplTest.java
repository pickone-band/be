//package com.pickone.global.security.service;
//
//import com.pickone.domain.user.model.entity.UserEntity;
//import com.pickone.domain.user.repository.UserJpaRepository;
//import com.pickone.global.security.dto.LoginRequest;
//import com.pickone.global.security.dto.PasswordResetRequest;
//import com.pickone.global.security.model.entity.UserPrincipal;
//import com.pickone.global.security.token.TokenProvider;
//import com.pickone.global.oauth2.service.UserFixture;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.*;
//
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//public class AuthServiceImplTest {
//
//  @InjectMocks
//  private AuthServiceImpl sut;
//
//  @Mock private UserJpaRepository userRepository;
//  @Mock private PasswordEncoder passwordEncoder;
//  @Mock private TokenProvider tokenProvider;
//
//  @BeforeEach
//  void setUp() {
//    MockitoAnnotations.openMocks(this);
//  }
//
//  @Test
//  @DisplayName("login: 유효한 자격이면 토큰을 반환한다")
//  void login_success() {
//    // given
//    String email = "user@example.com";
//    String password = "raw-password";
//    String encoded = "hashed-password";
//
//    UserEntity user = UserFixture.createPasswordUser(email, encoded);
//    LoginRequest request = new LoginRequest(email, password);
//
//    when(userRepository.findByProfileEmail(email)).thenReturn(Optional.of(user));
//    when(passwordEncoder.matches(password, encoded)).thenReturn(true);
//    when(tokenProvider.generateAccessToken(any(UserPrincipal.class))).thenReturn("access-token");
//    when(tokenProvider.generateRefreshToken(any(UserPrincipal.class))).thenReturn("refresh-token");
//
//    // when
//    var response = sut.login(request);
//
//    // then
//    assertThat(response.accessToken()).isEqualTo("access-token");
//    assertThat(response.refreshToken()).isEqualTo("refresh-token");
//    assertThat(response.userId()).isEqualTo(user.getId());
//  }
//
//  @Test
//  @DisplayName("login: 비밀번호가 일치하지 않으면 예외 발생")
//  void login_invalidPassword() {
//    // given
//    String email = "user@example.com";
//    UserEntity user = UserFixture.createPasswordUser(email, "hashed");
//    LoginRequest request = new LoginRequest(email, "wrong");
//
//    when(userRepository.findByProfileEmail(email)).thenReturn(Optional.of(user));
//    when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);
//
//    // expect
//    assertThatThrownBy(() -> sut.login(request))
//        .isInstanceOf(RuntimeException.class)
//        .hasMessage("INVALID_PASSWORD");
//  }
//
//  @Test
//  @DisplayName("logout: refreshToken을 블랙리스트 처리한다")
//  void logout_success() {
//    // given
//    String token = "refresh-token";
//
//    // when
//    sut.logout(token);
//
//    // then
//    verify(tokenProvider).blacklistToken(token);
//  }
//
//  @Test
//  @DisplayName("resetPassword: 유저의 비밀번호를 재설정한다")
//  void resetPassword_success() {
//    // given
//    String email = "user@example.com";
//    String newPassword = "new-password";
//    String encoded = "encoded-password";
//
//    UserEntity user = UserFixture.createPasswordUser(email, "old-password");
//    PasswordResetRequest request = new PasswordResetRequest(email, newPassword);
//
//    when(userRepository.findByProfileEmail(email)).thenReturn(Optional.of(user));
//    when(passwordEncoder.encode(newPassword)).thenReturn(encoded);
//
//    // when
//    sut.resetPassword(request);
//
//    // then
//    assertThat(user.getAuthInfo().getPassword()).isEqualTo(encoded);
//    verify(userRepository).save(user);
//  }
//}
