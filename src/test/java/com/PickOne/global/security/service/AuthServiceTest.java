    package com.PickOne.global.security.service;

    import com.PickOne.domain.consent.repository.ConsentJpaRepository;
    import com.PickOne.domain.term.model.entity.TermEntity;
    import com.PickOne.domain.term.service.TermService;
    import com.PickOne.domain.user.model.domain.*;
    import com.PickOne.domain.user.model.entity.UserEntity;
    import com.PickOne.domain.user.repository.UserJpaRepository;
    import com.PickOne.global.exception.BusinessException;
    import com.PickOne.global.security.dto.ChangePasswordRequest;
    import com.PickOne.global.security.dto.ConsentAgreementDto;
    import com.PickOne.global.security.dto.LoginRequest;
    import com.PickOne.global.security.dto.SignupRequestDto;
    import com.PickOne.global.security.repository.RefreshTokenRepository;
    import com.PickOne.global.security.repository.TokenBlacklistRepository;
    import org.junit.jupiter.api.BeforeEach;
    import org.junit.jupiter.api.DisplayName;
    import org.junit.jupiter.api.Test;
    import org.mockito.InjectMocks;
    import org.mockito.Mock;
    import org.mockito.MockitoAnnotations;
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

        @Mock
        private UserJpaRepository userJpaRepository;

        @Mock
        private PasswordEncoder passwordEncoder;

        @Mock
        private JwtService jwtService;

        @Mock
        private RefreshTokenRepository refreshTokenRepository;

        @Mock
        private TokenBlacklistRepository tokenBlacklistRepository;

        @Mock
        private TermService termService;

        @Mock
        private ConsentJpaRepository consentJpaRepository;

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);
            when(termService.getAll()).thenReturn(List.of(
                    new TermEntity(
                            1L,
                            "서비스 이용약관",
                            "내용입니다.",
                            "v1.0",
                            true,
                            LocalDateTime.now()
                    ),
                    new TermEntity(
                            2L,
                            "마케팅 정보 수신",
                            "동의하시면 이벤트 정보를 받아볼 수 있습니다.",
                            "v1.0",
                            false,
                            LocalDateTime.now()
                    )
            ));
        }

        @Test
        @DisplayName("회원가입 성공")
        void signup_success() {
            SignupRequestDto request = new SignupRequestDto(
                    "user@example.com",
                    "password123",
                    "닉네임",
                    LocalDate.of(1990, 1, 1),
                    Gender.MALE,
                    List.of(new ConsentAgreementDto(1L, true))
            );

            when(userJpaRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());
            when(userJpaRepository.findByNickname("닉네임")).thenReturn(Optional.empty());
            when(passwordEncoder.encode("password123")).thenReturn("encoded123");
            when(passwordEncoder.matches("password123", "encoded123")).thenReturn(true); // ✅ 추가
            when(jwtService.generateAccessToken(any())).thenReturn("access-token");
            when(jwtService.generateRefreshToken(any())).thenReturn("refresh-token");

            var result = authService.signup(request);

            assertThat(result.accessToken()).isNotBlank();
            assertThat(result.refreshToken()).isNotBlank();
            assertThat(result.email()).isEqualTo(request.email());
        }


        @Test
        @DisplayName("로그인 성공")
        void login_success() {
            String email = "test@email.com";

            UserEntity user = UserEntity.builder()
                    .email(email)
                    .password("encoded-password")
                    .nickname("nickname")
                    .profileImage("profile-img")
                    .role(Role.USER)
                    .isPublic(true)
                    .isOauth(false)
                    .gender(Gender.MALE)
                    .birthDate(LocalDate.of(1990, 1, 1))
                    .mbti(null)
                    .genres(List.of())
                    .build();

            when(userJpaRepository.findByEmail(email)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("rawpass", "encoded-password")).thenReturn(true); // ✅ 수정
            when(jwtService.generateAccessToken(any())).thenReturn("access-token");
            when(jwtService.generateRefreshToken(any())).thenReturn("refresh-token");

            LoginRequest request = new LoginRequest(email, "rawpass");

            var result = authService.login(request);

            assertThat(result.accessToken()).isNotNull();
            assertThat(result.refreshToken()).isNotNull();
            assertThat(result.email()).isEqualTo(email);
        }


        @Test
        @DisplayName("로그인 실패 - 비밀번호 불일치")
        void login_wrong_password() {
            String email = "fail@example.com";

            UserEntity user = UserEntity.builder()
                    .email(email)
                    .password("encoded-password")
                    .nickname("nickname")
                    .profileImage("profile-img")
                    .role(Role.USER)
                    .isPublic(true)
                    .isOauth(false)
                    .gender(Gender.MALE)
                    .birthDate(LocalDate.of(1990, 1, 1))
                    .mbti(null)
                    .genres(List.of())
                    .build();

            when(userJpaRepository.findByEmail(email)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("rawpass", "encodedPassword123")).thenReturn(true);

            LoginRequest request = new LoginRequest(email, "wrong");

            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("비밀번호가 일치하지 않습니다");
        }

        @Test
        @DisplayName("리프레시 토큰 성공")
        void refresh_success() {
            String refreshToken = "refresh.token.value";
            String email = "refresh@example.com";
            UserEntity user = UserEntity.builder()
                    .email(email)
                    .password("encoded-password")
                    .nickname("nickname")
                    .profileImage("profile-img")
                    .role(Role.USER)
                    .isPublic(true)
                    .isOauth(false)
                    .gender(Gender.MALE)
                    .birthDate(LocalDate.of(1990, 1, 1))
                    .mbti(null)
                    .genres(List.of())
                    .build();


            when(jwtService.validateRefreshToken(refreshToken)).thenReturn(true);
            when(jwtService.extractUsername(refreshToken)).thenReturn(email);
            when(userJpaRepository.findByEmail(email)).thenReturn(Optional.of(user));
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

        @Test
        @DisplayName("비밀번호 변경 성공")
        void change_password_success() {
            String token = "access.token.value";
            String email = "user@example.com";
            String oldEncodedPw = "encoded-old";
            String newRawPw = "new-password";

            UserEntity user = UserEntity.builder()
                    .email(email)
                    .password(oldEncodedPw)
                    .nickname("nickname")
                    .role(Role.USER)
                    .isOauth(false)
                    .isPublic(true)
                    .gender(Gender.MALE)
                    .birthDate(LocalDate.of(1990, 1, 1))
                    .genres(List.of())
                    .build();

            when(jwtService.extractUsername(token)).thenReturn(email);
            when(userJpaRepository.findByEmail(email)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("old-password", oldEncodedPw)).thenReturn(true);
            when(passwordEncoder.matches(newRawPw, oldEncodedPw)).thenReturn(false);
            when(passwordEncoder.encode(newRawPw)).thenReturn("encoded-new");

            ChangePasswordRequest request = new ChangePasswordRequest("old-password", newRawPw);
            authService.changePassword(token, request);

            assertThat(user.getPassword()).isEqualTo("encoded-new");
            verify(userJpaRepository, times(1)).save(user);
        }

        @Test
        @DisplayName("비밀번호 변경 실패 - 현재 비밀번호 불일치")
        void change_password_wrong_current() {
            String token = "access.token.value";
            String email = "user@example.com";

            UserEntity user = UserEntity.builder()
                    .email(email)
                    .password("encoded-old")
                    .nickname("nickname")
                    .role(Role.USER)
                    .isOauth(false)
                    .isPublic(true)
                    .gender(Gender.MALE)
                    .birthDate(LocalDate.of(1990, 1, 1))
                    .genres(List.of())
                    .build();

            when(jwtService.extractUsername(token)).thenReturn(email);
            when(userJpaRepository.findByEmail(email)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("wrong-password", "encoded-old")).thenReturn(false);

            ChangePasswordRequest request = new ChangePasswordRequest("wrong-password", "new-password");

            assertThatThrownBy(() -> authService.changePassword(token, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("비밀번호가 일치하지 않습니다");
        }

        @Test
        @DisplayName("비밀번호 변경 실패 - 기존 비밀번호와 동일")
        void change_password_same_as_old() {
            String token = "access.token.value";
            String email = "user@example.com";

            UserEntity user = UserEntity.builder()
                    .email(email)
                    .password("encoded-old")
                    .nickname("nickname")
                    .role(Role.USER)
                    .isOauth(false)
                    .isPublic(true)
                    .gender(Gender.MALE)
                    .birthDate(LocalDate.of(1990, 1, 1))
                    .genres(List.of())
                    .build();

            when(jwtService.extractUsername(token)).thenReturn(email);
            when(userJpaRepository.findByEmail(email)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("old-password", "encoded-old")).thenReturn(true);
            when(passwordEncoder.matches("old-password", "encoded-old")).thenReturn(true); // 새 비밀번호도 같다고 가정

            ChangePasswordRequest request = new ChangePasswordRequest("old-password", "old-password");

            assertThatThrownBy(() -> authService.changePassword(token, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessageContaining("새 비밀번호가 기존 비밀번호와 동일합니다");
        }

    }
