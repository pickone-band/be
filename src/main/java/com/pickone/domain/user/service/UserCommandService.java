package com.pickone.domain.user.service;

import com.pickone.domain.user.dto.SignupRequestDto;
import com.pickone.domain.user.dto.UpdatePreferenceRequestDto;
import com.pickone.domain.user.dto.UserResponseDto;
import com.pickone.domain.user.dto.UpdateProfileRequestDto;
import com.pickone.domain.user.dto.ChangePasswordRequestDto;
import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.global.oauth2.model.domain.OAuth2UserInfo;

public interface UserCommandService {
  UserResponseDto signup(SignupRequestDto dto);
  void updateProfile(Long userId, UpdateProfileRequestDto dto);
  void updatePreference(Long userId, UpdatePreferenceRequestDto dto);
  void changePassword(Long userId, ChangePasswordRequestDto dto);
  void lockUser(Long userId);
  void deleteUser(Long userId);
  UserEntity signupWithOAuth2(OAuth2UserInfo userInfo);
}
