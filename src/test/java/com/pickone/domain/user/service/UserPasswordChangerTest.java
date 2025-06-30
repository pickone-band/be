package com.pickone.domain.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.global.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserPasswordChangerTest {

  @Mock
  private PasswordEncoder passwordEncoder;
  @Mock private UserReader userReader;
  @InjectMocks
  private UserPasswordChanger passwordChanger;

  @Test
  void changePassword_validPassword_success() {
    UserEntity user = mock(UserEntity.class);
    when(userReader.findById(1L)).thenReturn(user);
    when(passwordEncoder.encode("newPassword")).thenReturn("encodedPwd");

    passwordChanger.changePassword(1L, "newPassword");

    verify(user).updatePassword("encodedPwd");
  }

  @Test
  void changePassword_invalidPassword_throws() {
    assertThrows(BusinessException.class, () -> {
      passwordChanger.changePassword(1L, "123");
    });
  }
}
