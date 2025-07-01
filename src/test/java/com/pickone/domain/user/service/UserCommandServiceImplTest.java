package com.pickone.domain.user.service;

import com.pickone.domain.user.dto.*;
import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.domain.user.model.factory.UserFactory;
import com.pickone.domain.user.model.policy.UserPolicy;
import com.pickone.domain.user.model.mapper.UserMapper;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.global.oauth2.model.domain.OAuth2UserInfo;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserCommandServiceImplTest {

  @Mock private UserJpaRepository userRepository;
  @Mock private UserFactory userFactory;
  @Mock private UserPolicy userPolicy;
  @Mock private PasswordEncoder passwordEncoder;
  @InjectMocks private UserCommandServiceImpl sut;

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
      SignupRequestDto dto = mock(SignupRequestDto.class);
      doNothing().when(userPolicy).validateSignup(dto);

      UserEntity user = mock(UserEntity.class);
      UserEntity saved = mock(UserEntity.class);

      when(userFactory.create(dto, passwordEncoder)).thenReturn(user);
      when(userRepository.save(user)).thenReturn(saved);

      UserResponseDto responseDto = mock(UserResponseDto.class);
      try (MockedStatic<UserMapper> staticMapper = mockStatic(UserMapper.class)) {
        staticMapper.when(() -> UserMapper.toDto(saved)).thenReturn(responseDto);

        UserResponseDto result = sut.signup(dto);

        assertThat(result).isSameAs(responseDto);
        verify(userPolicy).validateSignup(dto);
        verify(userFactory).create(dto, passwordEncoder);
        verify(userRepository).save(user);
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
      UpdateProfileRequestDto dto = new UpdateProfileRequestDto("nick", null, null, null);

      UserEntity user = mock(UserEntity.class);
      when(userRepository.findById(userId)).thenReturn(Optional.of(user));

      doNothing().when(user).updateProfile(any(), any(), any(), any());
      when(userRepository.save(user)).thenReturn(user);

      sut.updateProfile(userId, dto);

      verify(userRepository).findById(userId);
      verify(user).updateProfile(any(), any(), any(), any());
      verify(userRepository).save(user);
    }

    @Test
    @DisplayName("없는 유저는 예외 발생")
    void updateProfile_userNotFound() {
      Long userId = 2L;
      UpdateProfileRequestDto dto = mock(UpdateProfileRequestDto.class);

      when(userRepository.findById(userId)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> sut.updateProfile(userId, dto))
          .isInstanceOf(RuntimeException.class)
          .hasMessageContaining("Not found");
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

      UserEntity user = mock(UserEntity.class);
      when(userRepository.findById(userId)).thenReturn(Optional.of(user));
      doNothing().when(user).updatePreference(any());
      when(userRepository.save(user)).thenReturn(user);

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
      ChangePasswordRequestDto dto = new ChangePasswordRequestDto("new");

      UserEntity user = mock(UserEntity.class);
      when(userRepository.findById(userId)).thenReturn(Optional.of(user));

      when(passwordEncoder.encode(dto.newPassword())).thenReturn("hashed");
      doNothing().when(user).changePassword("hashed");
      when(userRepository.save(user)).thenReturn(user);

      sut.changePassword(userId, dto);

      verify(userRepository).findById(userId);
      verify(passwordEncoder).encode(dto.newPassword());
      verify(user).changePassword("hashed");
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
      UserEntity user = mock(UserEntity.class);

      when(userRepository.findById(userId)).thenReturn(Optional.of(user));
      doNothing().when(user).lock();
      when(userRepository.save(user)).thenReturn(user);

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
      doNothing().when(userRepository).deleteById(userId);

      sut.deleteUser(userId);

      verify(userRepository).deleteById(userId);
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
