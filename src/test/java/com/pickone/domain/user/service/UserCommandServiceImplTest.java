package com.pickone.domain.user.service;

import com.pickone.domain.consent.dto.ConsentRequestDto;
import com.pickone.domain.user.dto.*;
import com.pickone.domain.user.model.domain.Gender;
import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.domain.user.model.factory.UserFactory;
import com.pickone.domain.user.model.mapper.UserMapper;
import com.pickone.domain.user.model.policy.UserPolicy;
import com.pickone.domain.user.model.vo.UserProfile;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.global.common.enums.Mbti;
import com.pickone.global.email.dto.EmailSendRequestDto;
import com.pickone.global.email.service.EmailSendService;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.oauth2.model.domain.OAuth2UserInfo;
import com.pickone.global.security.service.EmailTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserCommandServiceImplTest {

  @Mock private UserJpaRepository userRepository;
  @Mock private UserFactory userFactory;
  @Mock private UserPolicy userPolicy;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private EmailTokenService emailTokenService;
  @Mock private EmailSendService emailSendService;
  @InjectMocks private UserCommandServiceImpl sut;

  private static final String DEFAULT_INTRO = "기본 자기소개입니다.";

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Nested
  @DisplayName("signup")
  class Signup {

    @Test
    @DisplayName("정상 회원가입")
    void signup_success() {
      SignupRequestDto dto = new SignupRequestDto(
          "test@example.com",
          "password123",
          "tester",
          LocalDate.of(1990, 1, 1),
          Gender.MALE,
          List.of(new ConsentRequestDto(1L, true))
      );


      doNothing().when(userPolicy).validateSignup(dto);

      UserProfile profile = UserProfile.of("tester", "test@example.com", LocalDate.of(1990, 1, 1), Gender.MALE, null, DEFAULT_INTRO);

      UserEntity user = mock(UserEntity.class);
      when(user.getProfile()).thenReturn(profile);

      UserEntity saved = mock(UserEntity.class);
      when(saved.getProfile()).thenReturn(profile);

      when(userFactory.create(dto, passwordEncoder)).thenReturn(user);
      when(userRepository.save(user)).thenReturn(saved);
      when(emailTokenService.createAndSaveToken(profile.getEmail())).thenReturn("dummy-token");
      doNothing().when(emailSendService).send(any(EmailSendRequestDto.class));

      try (MockedStatic<UserMapper> staticMapper = mockStatic(UserMapper.class)) {
        UserResponseDto responseDto = mock(UserResponseDto.class);
        staticMapper.when(() -> UserMapper.toDto(saved)).thenReturn(responseDto);

        UserResponseDto result = sut.signup(dto);

        assertThat(result).isSameAs(responseDto);
        verify(userPolicy).validateSignup(dto);
        verify(userFactory).create(dto, passwordEncoder);
        verify(userRepository).save(user);
        verify(emailTokenService).createAndSaveToken(profile.getEmail());
        verify(emailSendService).send(any(EmailSendRequestDto.class));
      }
    }
  }

  @Nested
  @DisplayName("updateProfile")
  class UpdateProfile {
    @Test
    @DisplayName("정상 프로필 변경")
    void updateProfile_success() {
      Long userId = 1L;
      UpdateProfileRequestDto dto = new UpdateProfileRequestDto("newNick", Mbti.INFP, "안녕하세요. 새로운 소개입니다.");


      UserEntity user = spy(UserEntity.of(
          "test@example.com",
          "encryptedPassword",
          "oldNick",
          Gender.FEMALE,
          LocalDate.of(1990, 1, 1),
          Mbti.ENFJ,
          List.of(),
          DEFAULT_INTRO
      ));

      when(userRepository.findById(userId)).thenReturn(Optional.of(user));
      when(userRepository.existsByProfileNickname(dto.nickname())).thenReturn(false);

      sut.updateProfile(userId, dto);

      verify(userRepository).findById(userId);
      verify(userRepository).existsByProfileNickname(dto.nickname());
      verify(userRepository).save(user);

      assertThat(user.getProfile().getNickname()).isEqualTo(dto.nickname());
      assertThat(user.getProfile().getMbti()).isEqualTo(dto.mbti());
    }

    @Test
    @DisplayName("없는 유저는 예외 발생")
    void updateProfile_userNotFound() {
      Long userId = 2L;
      UpdateProfileRequestDto dto = new UpdateProfileRequestDto("newNick", Mbti.INFP, "안녕하세요. 새로운 소개입니다.");


      when(userRepository.findById(userId)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> sut.updateProfile(userId, dto))
          .isInstanceOf(BusinessException.class);
    }
  }

  @Nested
  @DisplayName("updatePreference")
  class UpdatePreference {
    @Test
    @DisplayName("정상 선호 변경")
    void updatePreference_success() {
      Long userId = 1L;
      UpdatePreferenceRequestDto dto = new UpdatePreferenceRequestDto(List.of());

      UserEntity realUser = UserEntity.of(
          "test2@example.com",
          "password",
          "nickname",
          Gender.MALE,
          LocalDate.of(1985, 5, 5),
          Mbti.ENFP,
          List.of(),
          DEFAULT_INTRO
      );
      UserEntity user = spy(realUser);

      when(userRepository.findById(userId)).thenReturn(Optional.of(user));

      sut.updatePreference(userId, dto);

      verify(userRepository).findById(userId);
      verify(user).updatePreference(any());
      verify(userRepository).save(user);
    }
  }

  @Nested
  @DisplayName("changePassword")
  class ChangePassword {
    @Test
    @DisplayName("비밀번호 변경 성공")
    void changePassword_success() {
      Long userId = 1L;
      ChangePasswordRequestDto dto = new ChangePasswordRequestDto("newPassword");

      UserEntity realUser = UserEntity.of(
          "test3@example.com",
          "oldPassword",
          "nickname3",
          Gender.FEMALE,
          LocalDate.of(1995, 6, 6),
          Mbti.INTJ,
          List.of(),
          DEFAULT_INTRO
      );
      UserEntity user = spy(realUser);

      when(userRepository.findById(userId)).thenReturn(Optional.of(user));
      when(passwordEncoder.encode(dto.newPassword())).thenReturn("hashedPassword");

      sut.changePassword(userId, dto);

      verify(userRepository).findById(userId);
      verify(passwordEncoder).encode(dto.newPassword());
      verify(user).changePassword("hashedPassword");
      verify(userRepository).save(user);
    }
  }

  @Nested
  @DisplayName("lockUser")
  class LockUser {
    @Test
    @DisplayName("유저 잠금 성공")
    void lockUser_success() {
      Long userId = 1L;

      UserEntity realUser = UserEntity.of(
          "test4@example.com",
          "password",
          "nickname4",
          Gender.MALE,
          LocalDate.of(1992, 7, 7),
          Mbti.ISTP,
          List.of(),
          DEFAULT_INTRO
      );
      UserEntity user = spy(realUser);

      when(userRepository.findById(userId)).thenReturn(Optional.of(user));

      sut.lockUser(userId);

      verify(userRepository).findById(userId);
      verify(user).lock();
      verify(userRepository).save(user);
    }
  }

  @Nested
  @DisplayName("deleteUser")
  class DeleteUser {
    @Test
    @DisplayName("정상적으로 유저 삭제")
    void deleteUser_success() {
      Long userId = 1L;

      when(userRepository.existsById(userId)).thenReturn(true);

      sut.deleteUser(userId);

      verify(userRepository).existsById(userId);
      verify(userRepository).deleteById(userId);
    }

    @Test
    @DisplayName("없는 유저 삭제 시 예외 발생")
    void deleteUser_notFound() {
      Long userId = 2L;

      when(userRepository.existsById(userId)).thenReturn(false);

      assertThatThrownBy(() -> sut.deleteUser(userId))
          .isInstanceOf(BusinessException.class);
    }
  }

  @Nested
  @DisplayName("signupWithOAuth2")
  class SignupWithOAuth2 {
    @Test
    @DisplayName("OAuth2로 정상 회원가입")
    void signupWithOAuth2_success() {
      OAuth2UserInfo userInfo = mock(OAuth2UserInfo.class);
      doNothing().when(userPolicy).validateSignupWithOAuth2(userInfo);

      UserEntity user = mock(UserEntity.class);
      UserEntity saved = mock(UserEntity.class);

      when(userFactory.createWithOAuth2(userInfo)).thenReturn(user);
      when(userRepository.save(user)).thenReturn(saved);

      UserEntity result = sut.signupWithOAuth2(userInfo);

      assertThat(result).isSameAs(saved);
      verify(userPolicy).validateSignupWithOAuth2(userInfo);
      verify(userFactory).createWithOAuth2(userInfo);
      verify(userRepository).save(user);
    }
  }
}
