package com.pickone.domain.user.service;

import com.pickone.domain.user.dto.*;
import com.pickone.domain.user.entity.User;
import com.pickone.domain.user.factory.UserFactory;
import com.pickone.domain.user.mapper.UserMapper;
import com.pickone.domain.user.model.domain.Gender;
import com.pickone.domain.user.model.vo.UserProfile;
import com.pickone.domain.user.policy.UserPolicy;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.global.email.dto.EmailSendRequest;
import com.pickone.global.email.service.EmailSendService;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.oauth2.model.domain.OAuth2UserInfo;
import com.pickone.global.security.service.EmailTokenService;
import com.pickone.global.common.enums.Mbti;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserCommandServiceImplTest {

  @InjectMocks
  private UserCommandServiceImpl userCommandService;

  @Mock
  private UserJpaRepository userRepository;
  @Mock
  private UserFactory userFactory;
  @Mock
  private UserPolicy userPolicy;
  @Mock
  private PasswordEncoder passwordEncoder;
  @Mock
  private EmailTokenService emailTokenService;
  @Mock
  private EmailSendService emailSendService;
  @Mock
  private UserMapper userMapper;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void signup_success() {
    SignupRequest request = new SignupRequest(
        "user@example.com", "password", "nickname",
        LocalDate.of(1990, 1, 1), Gender.MALE, List.of()
    );

    User mockUser = mock(User.class);
    UserProfile profile = UserProfile.of("nickname", "user@example.com", LocalDate.of(1990, 1, 1), Gender.MALE, Mbti.ENFP, null, null);
    when(mockUser.getProfile()).thenReturn(profile);

    when(userFactory.create(eq(request), any())).thenReturn(mockUser);
    when(userRepository.save(mockUser)).thenReturn(mockUser);
    when(emailTokenService.createAndSaveToken("user@example.com")).thenReturn("test-token");
    when(userMapper.toDto(mockUser)).thenReturn(mock(UserResponse.class));

    UserResponse response = userCommandService.signup(request);

    // then
    assertNotNull(response);

    // ✅ 비동기 메서드가 호출될 때까지 기다렸다가 검증
    await().atMost(1, TimeUnit.SECONDS)
        .untilAsserted(() -> verify(emailSendService).sendAsync(any(EmailSendRequest.class)));
  }

  @Test
  void updateProfile_success() {
    Long userId = 1L;
    UpdateProfileRequest request = new UpdateProfileRequest("newNickname", Mbti.INFP, null, "소개");

    User mockUser = mock(User.class);
    UserProfile profile = UserProfile.of("oldNick", "user@example.com", LocalDate.of(1990, 1, 1), Gender.FEMALE, Mbti.ENFJ, null, null);
    when(mockUser.getProfile()).thenReturn(profile);

    when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
    when(userRepository.existsByProfileNickname("newNickname")).thenReturn(false);

    userCommandService.updateProfile(userId, request);

    verify(mockUser).updateProfile(
        eq("newNickname"),
        eq(profile.getBirthDate()),
        eq(profile.getGender()),
        eq(Mbti.INFP),
        eq(null),
        eq("소개")
    );
  }

  @Test
  void updatePreference_success() {
    Long userId = 1L;
    UpdatePreferenceRequest request = new UpdatePreferenceRequest(List.of());

    User user = mock(User.class);
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));

    userCommandService.updatePreference(userId, request);

    verify(user).updatePreference(request.genres());
  }

  @Test
  void changePassword_success() {
    Long userId = 1L;
    ChangePasswordRequest request = new ChangePasswordRequest("newPw");

    User user = mock(User.class);
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(passwordEncoder.encode("newPw")).thenReturn("encoded");

    userCommandService.changePassword(userId, request);

    verify(user).changePassword("encoded");
  }

  @Test
  void lockUser_success() {
    User user = mock(User.class);
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));

    userCommandService.lockUser(1L);
    verify(user).lock();
  }

  @Test
  void deleteUser_success() {
    when(userRepository.existsById(1L)).thenReturn(true);
    userCommandService.deleteUser(1L);
    verify(userRepository).deleteById(1L);
  }

  @Test
  void signupWithOAuth2_success() {
    OAuth2UserInfo userInfo = mock(OAuth2UserInfo.class);
    when(userInfo.getEmail()).thenReturn("oauth@example.com");

    User user = mock(User.class);
    when(userFactory.createWithOAuth2(userInfo)).thenReturn(user);
    when(userRepository.save(user)).thenReturn(user);

    User saved = userCommandService.signupWithOAuth2(userInfo);
    assertEquals(user, saved);
  }

  @Test
  void deleteUser_notFound() {
    when(userRepository.existsById(99L)).thenReturn(false);
    assertThrows(BusinessException.class, () -> userCommandService.deleteUser(99L));
  }
}
