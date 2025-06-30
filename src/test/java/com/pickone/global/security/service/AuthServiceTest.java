package com.pickone.global.security.service;

import com.pickone.domain.consent.dto.ConsentTermtDto;
import com.pickone.domain.consent.repository.ConsentJpaRepository;
import com.pickone.domain.term.model.entity.TermEntity;
import com.pickone.domain.term.service.TermService;
import com.pickone.domain.user.dto.InstrumentInfoDto;
import com.pickone.domain.user.dto.SignupRequestDto;
import com.pickone.domain.user.model.domain.*;
import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.domain.user.service.UserJoinService;
import com.pickone.global.common.enums.Genre;
import com.pickone.global.common.enums.Instrument;
import com.pickone.global.common.enums.Mbti;
import com.pickone.global.common.enums.Proficiency;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.security.dto.*;
import com.pickone.global.security.repository.RefreshTokenRepository;
import com.pickone.global.security.repository.TokenBlacklistRepository;
import com.pickone.global.verification.service.EmailVerificationService;
import java.lang.reflect.Field;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

  @InjectMocks
  private AuthServiceImpl authService;

  @Mock private UserJpaRepository userJpaRepository;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private JwtService jwtService;
  @Mock private RefreshTokenRepository refreshTokenRepository;
  @Mock private TokenBlacklistRepository tokenBlacklistRepository;
  @Mock private TermService termService;
  @Mock private ConsentJpaRepository consentJpaRepository;
  @Mock private EmailVerificationService emailVerificationService;
  @Mock private UserJoinService userJoinService;

  private TermEntity requiredTerm;
  private TermEntity optionalTerm;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    // 약관 엔티티 생성 및 ID 강제 세팅 (reflection)
    requiredTerm = TermEntity.create("서비스 이용약관", "내용입니다.", "v1.0", true, LocalDateTime.now());
    optionalTerm = TermEntity.create("마케팅 정보 수신", "동의하시면 이벤트 정보를 받아볼 수 있습니다.", "v1.0", false, LocalDateTime.now());
    setIdByReflection(requiredTerm, 1L);
    setIdByReflection(optionalTerm, 2L);

    // 약관 서비스 mock
    when(termService.getAll()).thenReturn(List.of(requiredTerm, optionalTerm));
    // UserJoinService join은 void
    doNothing().when(userJoinService).join(any(SignupRequestDto.class));

    // 비밀번호/토큰 등 mock
    when(passwordEncoder.encode(anyString())).thenReturn("encoded123");
    when(jwtService.generateAccessToken(any())).thenReturn("access-token");
    when(jwtService.generateRefreshToken(any())).thenReturn("refresh-token");
    doNothing().when(emailVerificationService).sendVerificationEmail(any());

    // --- 핵심: 회원가입 성공 후 findByEmail이 동적으로 유저를 반환하도록 설정 ---
    // 실제 userJpaRepository는 DB 저장 결과를 반환하지만, Mock에서는 동적으로 맞춤형 반환 필요
    // "user@example.com"에 대해 가입한 유저 반환, 그 외는 empty
    when(userJpaRepository.findByProfile_Email(anyString())).thenAnswer((Answer<Optional<UserEntity>>) invocation -> {
      String email = invocation.getArgument(0, String.class);
      if ("user@example.com".equals(email)) {
        UserEntity user = UserEntity.of(
            email, "encoded123", "nickname",
            Gender.MALE, LocalDate.of(1990, 1, 1), null, List.of()
        );
        user.getStatus().verify();
        return Optional.of(user);
      }
      if ("test@email.com".equals(email)) {
        UserEntity user = UserEntity.of(
            email, "encoded-password", "nickname",
            Gender.MALE, LocalDate.of(1990, 1, 1), null, List.of()
        );
        user.getStatus().verify();
        return Optional.of(user);
      }
      if ("refresh@example.com".equals(email)) {
        UserEntity user = UserEntity.of(
            email, "encoded-password", "nickname",
            Gender.MALE, LocalDate.of(1990, 1, 1), null, List.of()
        );
        user.getStatus().verify();
        return Optional.of(user);
      }
      if ("fail@example.com".equals(email)) {
        UserEntity user = UserEntity.of(
            email, "encoded-password", "nickname",
            Gender.MALE, LocalDate.of(1990, 1, 1), null, List.of()
        );
        user.getStatus().verify();
        return Optional.of(user);
      }
      // "noagree@example.com" 등 그 외에는 empty
      return Optional.empty();
    });
    // nickname은 모두 empty 반환
    when(userJpaRepository.findByProfile_Nickname(anyString())).thenReturn(Optional.empty());
  }

  @Test
  @DisplayName("회원가입 성공 - 필수 약관 동의 시 정상 처리")
  void signupSucceedsIfRequiredTermsAgreed() {
    List<ConsentTermtDto> agreements = List.of(
        new ConsentTermtDto(1L, true),
        new ConsentTermtDto(2L, false)
    );
    SignupRequestDto request = new SignupRequestDto(
        "user@example.com",
        "password123",
        "nickname",
        LocalDate.of(1990, 1, 1),
        Gender.MALE,
        "010-1234-5678",
        List.of(new InstrumentInfoDto(Instrument.ACOUSTIC_GUITAR, Proficiency.BEGINNER)),
        List.of(Genre.ACOUSTIC),
        Mbti.ENFP,
        agreements
    );
    // 첫 호출은 empty (가입 전), 두 번째는 가입된 유저
    UserEntity user = UserEntity.of(
        "user@example.com", "encoded123", "nickname",
        Gender.MALE, LocalDate.of(1990, 1, 1), null, List.of()
    );
    user.getStatus().verify();
    when(userJpaRepository.findByProfile_Email("user@example.com"))
        .thenReturn(Optional.empty())
        .thenReturn(Optional.of(user));
    when(userJpaRepository.findByProfile_Nickname(anyString())).thenReturn(Optional.empty());

    var result = authService.signup(request);

    assertThat(result.accessToken()).isNotBlank();
    assertThat(result.refreshToken()).isNotBlank();
    assertThat(result.email()).isEqualTo(request.email());
  }


  @Test
  @DisplayName("회원가입 실패 - 필수 약관 미동의 시 예외 발생")
  void signupFailsIfRequiredTermsNotAgreed() {
    List<ConsentTermtDto> agreements = List.of(
        new ConsentTermtDto(1L, false)
    );
    SignupRequestDto request = new SignupRequestDto(
        "noagree@example.com",
        "password123",
        "nickname",
        LocalDate.of(1990, 1, 1),
        Gender.MALE,
        "010-1234-5678",
        List.of(),
        List.of(),
        Mbti.ENFP,
        agreements
    );
    assertThatThrownBy(() -> authService.signup(request))
        .isInstanceOf(BusinessException.class);
  }

  @Test
  @DisplayName("로그인 성공 - 이메일 인증 완료 시")
  void loginSucceedsIfEmailVerified() {
    String email = "test@email.com";
    UserEntity user = UserEntity.of(
        email, "encoded-password", "nickname",
        Gender.MALE, LocalDate.of(1990, 1, 1), null, List.of()
    );
    user.verifyEmail();

    when(userJpaRepository.findByProfile_Email(email)).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("rawpass", "encoded-password")).thenReturn(true);

    LoginRequest request = new LoginRequest(email, "rawpass");
    var result = authService.login(request);

    assertThat(result.accessToken()).isNotNull();
    assertThat(result.refreshToken()).isNotNull();
    assertThat(result.email()).isEqualTo(email);
  }

  @Test
  @DisplayName("로그인 실패 - 이메일 미인증 시 예외 발생")
  void loginFailsIfEmailNotVerified() {
    String email = "test@email.com";
    UserEntity user = UserEntity.of(
        email, "encoded-password", "nickname",
        Gender.MALE, LocalDate.of(1990, 1, 1), null, List.of()
    );
    // verify 미호출
    when(userJpaRepository.findByProfile_Email(email)).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("rawpass", "encoded-password")).thenReturn(true);

    LoginRequest request = new LoginRequest(email, "rawpass");
    assertThatThrownBy(() -> authService.login(request))
        .isInstanceOf(BusinessException.class);
  }

  @Test
  @DisplayName("로그인 실패 - 비밀번호 불일치 시 예외 발생")
  void loginFailsIfPasswordMismatch() {
    String email = "fail@example.com";
    when(passwordEncoder.matches("wrong", "encoded-password")).thenReturn(false);

    LoginRequest request = new LoginRequest(email, "wrong");
    assertThatThrownBy(() -> authService.login(request))
        .isInstanceOf(BusinessException.class);
  }

  @Test
  @DisplayName("리프레시 토큰 성공")
  void refreshSucceeds() {
    String refreshToken = "refresh.token.value";
    String email = "refresh@example.com";
    when(jwtService.validateRefreshToken(refreshToken)).thenReturn(true);
    when(jwtService.extractUsername(refreshToken)).thenReturn(email);
    when(jwtService.generateAccessToken(any())).thenReturn("access-token");
    when(jwtService.generateRefreshToken(any())).thenReturn("refresh-token");

    var result = authService.refresh(refreshToken);

    assertThat(result.accessToken()).isNotNull();
    assertThat(result.refreshToken()).isNotNull();
  }

  @Test
  @DisplayName("로그아웃 - 블랙리스트 추가")
  void logoutSucceeds() {
    String accessToken = "access.token.value";
    doNothing().when(jwtService).blacklistToken(accessToken);

    authService.logout(accessToken);

    verify(jwtService, times(1)).blacklistToken(accessToken);
  }

  @Test
  @DisplayName("비밀번호 변경 성공")
  void changePasswordSucceeds() {
    String token = "access.token.value";
    String email = "user@example.com";
    String oldEncodedPw = "encoded-old";
    String newRawPw = "new-password";

    UserEntity user = UserEntity.of(
        email, oldEncodedPw, "nickname",
        Gender.MALE, LocalDate.of(1990, 1, 1), null, List.of()
    );
    user.getStatus().verify();

    when(jwtService.extractUsername(token)).thenReturn(email);
    when(userJpaRepository.findByProfile_Email(email)).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("old-password", oldEncodedPw)).thenReturn(true);
    when(passwordEncoder.matches(newRawPw, oldEncodedPw)).thenReturn(false);
    when(passwordEncoder.encode(newRawPw)).thenReturn("encoded-new");

    ChangePasswordRequest request = new ChangePasswordRequest("old-password", newRawPw);
    authService.changePassword(token, request);

    assertThat(user.getProfile().getPassword()).isEqualTo("encoded-new");
    verify(userJpaRepository, times(1)).save(user);
  }

  @Test
  @DisplayName("비밀번호 변경 실패 - 현재 비밀번호 불일치 시 예외 발생")
  void changePasswordFailsIfCurrentMismatch() {
    String token = "access.token.value";
    String email = "user@example.com";

    UserEntity user = UserEntity.of(
        email, "encoded-old", "nickname",
        Gender.MALE, LocalDate.of(1990, 1, 1), null, List.of()
    );
    user.getStatus().verify();

    when(jwtService.extractUsername(token)).thenReturn(email);
    when(userJpaRepository.findByProfile_Email(email)).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("wrong-password", "encoded-old")).thenReturn(false);

    ChangePasswordRequest request = new ChangePasswordRequest("wrong-password", "new-password");

    assertThatThrownBy(() -> authService.changePassword(token, request))
        .isInstanceOf(BusinessException.class);
  }

  @Test
  @DisplayName("비밀번호 변경 실패 - 기존 비밀번호와 동일 시 예외 발생")
  void changePasswordFailsIfSameAsOld() {
    String token = "access.token.value";
    String email = "user@example.com";

    UserEntity user = UserEntity.of(
        email, "encoded-old", "nickname",
        Gender.MALE, LocalDate.of(1990, 1, 1), null, List.of()
    );
    user.getStatus().verify();

    when(jwtService.extractUsername(token)).thenReturn(email);
    when(userJpaRepository.findByProfile_Email(email)).thenReturn(Optional.of(user));
    when(passwordEncoder.matches("old-password", "encoded-old")).thenReturn(true);
    when(passwordEncoder.matches("old-password", "encoded-old")).thenReturn(true); // same password

    ChangePasswordRequest request = new ChangePasswordRequest("old-password", "old-password");

    assertThatThrownBy(() -> authService.changePassword(token, request))
        .isInstanceOf(BusinessException.class);
  }

  private void setIdByReflection(Object entity, Long id) {
    try {
      Field field = entity.getClass().getDeclaredField("id");
      field.setAccessible(true);
      field.set(entity, id);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
