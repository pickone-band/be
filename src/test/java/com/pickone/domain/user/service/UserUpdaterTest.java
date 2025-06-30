package com.pickone.domain.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pickone.domain.user.dto.UserUpdateRequestDto;
import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.global.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserUpdaterTest {

  @Mock private UserReader userReader;
  @Mock
  private UserInstrumentService userInstrumentService;
  @Mock private UserPreferenceService userPreferenceService;
  @Mock private PasswordEncoder passwordEncoder;
  @InjectMocks
  private UserUpdater userUpdater;

  @Test
  void updateUser_success() {
    UserEntity user = mock(UserEntity.class);
    UserUpdateRequestDto dto = mock(UserUpdateRequestDto.class);

    when(userReader.findById(1L)).thenReturn(user);
    when(dto.nickname()).thenReturn("newNick");
    when(dto.profileImage()).thenReturn("newImage");
    when(dto.mbti()).thenReturn(null);
    when(dto.instruments()).thenReturn(null);
    when(dto.genres()).thenReturn(null);
    when(dto.newPassword()).thenReturn("newPassword");
    when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

    userUpdater.updateUser(1L, dto);

    verify(user).updateProfile("newNick", "newImage", null);
    verify(userInstrumentService, never()).update(any(), any());
    verify(userPreferenceService, never()).update(any(), any());
    verify(user).updatePassword("encodedNewPassword");
  }

  @Test
  void updateUser_invalidPassword_throws() {
    UserEntity user = mock(UserEntity.class);
    UserUpdateRequestDto dto = mock(UserUpdateRequestDto.class);

    when(userReader.findById(1L)).thenReturn(user);
    when(dto.nickname()).thenReturn("nick");
    when(dto.profileImage()).thenReturn("img");
    when(dto.mbti()).thenReturn(null);
    when(dto.instruments()).thenReturn(null);
    when(dto.genres()).thenReturn(null);
    when(dto.newPassword()).thenReturn("123"); // too short

    assertThrows(BusinessException.class, () -> userUpdater.updateUser(1L, dto));
  }
}
