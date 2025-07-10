package com.pickone.domain.user.service;

import com.pickone.domain.user.dto.*;
import com.pickone.domain.user.factory.UserFactory;
import com.pickone.domain.user.entity.User;


import com.pickone.domain.user.mapper.UserMapper;
import com.pickone.domain.user.policy.UserPolicy;
import com.pickone.domain.user.repository.UserJpaRepository;

import com.pickone.global.email.dto.EmailSendRequest;
import com.pickone.global.email.service.EmailSendService;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import com.pickone.global.oauth2.model.domain.OAuth2UserInfo;
import com.pickone.global.security.service.EmailTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCommandServiceImpl implements UserCommandService {

  private final UserJpaRepository userRepository;
  private final UserFactory userFactory;
  private final UserPolicy userPolicy;
  private final PasswordEncoder passwordEncoder;
  private final EmailTokenService emailTokenService;
  private final EmailSendService emailSendService;
  private final UserMapper userMapper;

  @Transactional
  @Override
  public UserResponse signup(SignupRequest request) {
    log.info("[UserCommandService] 회원가입 요청: email={}, nickname={}", request.email(), request.nickname());

    userPolicy.validateSignup(request);
    User user = userFactory.create(request, passwordEncoder);
    User saved = userRepository.save(user);

    String token = emailTokenService.createAndSaveToken(saved.getProfile().getEmail());
    EmailSendRequest emailRequest = new EmailSendRequest(
        saved.getProfile().getEmail(),
        "이메일 인증",
        "인증 링크: http://3.35.49.195:8080/api/auth/verify-email?token=" + token
    );
    emailSendService.sendAsync(emailRequest);

    return userMapper.toDto(saved);
  }

  @Transactional
  @Override
  public void updateProfile(Long userId, UpdateProfileRequest request) {
    log.info("[UserCommandService] 프로필 수정 요청: userId={}, nickname={}", userId, request.nickname());

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));

    if (userRepository.existsByProfileNickname(request.nickname()) &&
        !request.nickname().equals(user.getProfile().getNickname())) {
      throw new BusinessException(ErrorCode.DUPLICATE_NICKNAME);
    }

    user.updateProfile(
        request.nickname(),
        user.getProfile().getBirthDate(),
        user.getProfile().getGender(),
        request.mbti() != null ? request.mbti() : user.getProfile().getMbti(),
        request.profileImageUrl() != null ? request.profileImageUrl() : user.getProfile().getProfileImageUrl(),
        request.introduction()
    );
  }

  @Transactional
  @Override
  public void updatePreference(Long userId, UpdatePreferenceRequest request) {
    log.info("[UserCommandService] 선호 장르 수정 요청: userId={}, genres={}", userId, request.genres());

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));
    user.updatePreference(request.genres());
  }

  @Transactional
  @Override
  public void changePassword(Long userId, ChangePasswordRequest request) {
    log.info("[UserCommandService] 비밀번호 변경 요청: userId={}", userId);

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));
    user.changePassword(passwordEncoder.encode(request.newPassword()));
  }

  @Transactional
  @Override
  public void lockUser(Long userId) {
    log.info("[UserCommandService] 사용자 잠금 요청: userId={}", userId);

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));
    user.lock();
  }

  @Transactional
  @Override
  public void deleteUser(Long userId) {
    log.info("[UserCommandService] 사용자 삭제 요청: userId={}", userId);

    if (!userRepository.existsById(userId)) {
      throw new BusinessException(ErrorCode.USER_INFO_NOT_FOUND);
    }
    userRepository.deleteById(userId);
  }

  @Transactional
  public User signupWithOAuth2(OAuth2UserInfo userInfo) {
    log.info("[UserCommandService] 소셜 회원가입 요청: email={}", userInfo.getEmail());

    userPolicy.validateSignupWithOAuth2(userInfo);
    User user = userFactory.createWithOAuth2(userInfo);
    return userRepository.save(user);
  }
}
