package com.pickone.domain.user.service;

import com.pickone.domain.user.dto.SignupRequestDto;
import com.pickone.domain.user.dto.UpdatePreferenceRequestDto;
import com.pickone.domain.user.dto.UserResponseDto;
import com.pickone.domain.user.dto.UpdateProfileRequestDto;
import com.pickone.domain.user.dto.ChangePasswordRequestDto;
import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.domain.user.model.factory.UserFactory;
import com.pickone.domain.user.model.policy.UserPolicy;
import com.pickone.domain.user.model.mapper.UserMapper;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.global.oauth2.model.domain.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserCommandServiceImpl implements UserCommandService {
  private final UserJpaRepository userRepository;
  private final UserFactory userFactory;
  private final UserPolicy userPolicy;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  @Override
  public UserResponseDto signup(SignupRequestDto dto) {
    userPolicy.validateSignup(dto);
    UserEntity user = userFactory.create(dto, passwordEncoder);
    UserEntity saved = userRepository.save(user);
    return UserMapper.toDto(saved);
  }

  @Transactional
  @Override
  public void updateProfile(Long userId, UpdateProfileRequestDto dto) {
    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("Not found"));
    user.updateProfile(dto.nickname(), dto.birthDate(), dto.gender(), dto.mbti());
    userRepository.save(user);
  }

  @Transactional
  @Override
  public void updatePreference(Long userId, UpdatePreferenceRequestDto dto) {
    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("Not found"));
    user.updatePreference(dto.genres());
    userRepository.save(user);
  }

  @Transactional
  @Override
  public void changePassword(Long userId, ChangePasswordRequestDto dto) {
    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("Not found"));
    user.changePassword(passwordEncoder.encode(dto.newPassword()));
    userRepository.save(user);
  }

  @Transactional
  @Override
  public void lockUser(Long userId) {
    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("Not found"));
    user.lock();
    userRepository.save(user);
  }

  @Transactional
  @Override
  public void deleteUser(Long userId) {
    userRepository.deleteById(userId);
  }

  @Transactional
  public UserEntity signupWithOAuth2(OAuth2UserInfo userInfo) {
    userPolicy.validateSignupWithOAuth2(userInfo);
    UserEntity user = userFactory.createWithOAuth2(userInfo);
    return userRepository.save(user);
  }
}
