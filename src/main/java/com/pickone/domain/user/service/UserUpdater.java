package com.pickone.domain.user.service;


import com.pickone.domain.user.dto.UserUpdateRequestDto;
import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

  @Service
  @RequiredArgsConstructor
  public class UserUpdater {

    private final UserReader userReader;
    private final UserInstrumentService userInstrumentService;
    private final UserPreferenceService userPreferenceService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void updateUser(Long userId, UserUpdateRequestDto dto) {
      UserEntity user = userReader.findById(userId);

      user.updateProfile(dto.nickname(), dto.profileImage(), dto.mbti());

      if (dto.instruments() != null) {
        userInstrumentService.update(user, dto.instruments());
      }

      if (dto.genres() != null) {
        userPreferenceService.update(user, dto.genres());
      }

      if (dto.newPassword() != null && !dto.newPassword().isBlank()) {
        if (dto.newPassword().length() < 6) {
          throw new BusinessException(ErrorCode.INVALID_PASSWORD_FORMAT);
        }
        String encodedPassword = passwordEncoder.encode(dto.newPassword());
        user.updatePassword(encodedPassword);
      }
    }
  }

