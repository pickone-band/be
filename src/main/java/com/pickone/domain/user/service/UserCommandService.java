package com.pickone.domain.user.service;

import com.pickone.domain.user.dto.ChangePasswordRequest;
import com.pickone.domain.user.dto.SignupRequest;
import com.pickone.domain.user.dto.UpdatePreferenceRequest;
import com.pickone.domain.user.dto.UpdateProfileRequest;
import com.pickone.domain.user.dto.UserResponse;
import com.pickone.domain.user.entity.User;
import com.pickone.global.oauth2.model.domain.OAuth2UserInfo;

public interface UserCommandService {
  UserResponse signup(SignupRequest dto);
  void updateProfile(Long userId, UpdateProfileRequest dto);
  void updatePreference(Long userId, UpdatePreferenceRequest dto);
  void changePassword(Long userId, ChangePasswordRequest dto);
  void lockUser(Long userId);
  void deleteUser(Long userId);
  User signupWithOAuth2(OAuth2UserInfo userInfo);
}
