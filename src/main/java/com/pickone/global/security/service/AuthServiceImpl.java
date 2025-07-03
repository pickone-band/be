package com.pickone.global.security.service;

import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import com.pickone.global.security.dto.LoginRequest;
import com.pickone.global.security.dto.LoginResponse;
import com.pickone.global.security.dto.PasswordResetRequest;
import com.pickone.global.security.model.entity.UserPrincipal;
import com.pickone.global.security.token.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final UserJpaRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final TokenProvider tokenProvider;

  @Override
  public LoginResponse login(LoginRequest request) {
    UserEntity user = userRepository.findByProfileEmail(request.email())
        .orElseThrow(() -> new BusinessException(ErrorCode.LOGIN_USER_NOT_FOUND));

    if (!passwordEncoder.matches(request.password(), user.getAuthInfo().getPassword())) {
      throw new BusinessException(ErrorCode.INVALID_PASSWORD_CREDENTIAL);
    }

    if (!user.getStatus().isVerified()) {
      throw new BusinessException(ErrorCode.EMAIL_NOT_VERIFIED);
    }

    var principal = UserPrincipal.from(user);
    String accessToken = tokenProvider.generateAccessToken(principal);
    String refreshToken = tokenProvider.generateRefreshToken(principal);

    return new LoginResponse(accessToken, refreshToken, user.getId());
  }

  @Override
  public void logout(String refreshToken) {
    if (refreshToken == null || refreshToken.isBlank()) {
      throw new BusinessException(ErrorCode.TOKEN_NOT_FOUND);
    }

    boolean isBlacklisted = tokenProvider.isTokenBlacklisted(refreshToken);
    if (isBlacklisted) {
      throw new BusinessException(ErrorCode.ALREADY_LOGGED_OUT);
    }

    tokenProvider.blacklistToken(refreshToken);
  }

  @Override
  public void resetPassword(PasswordResetRequest request) {
    UserEntity user = userRepository.findByProfileEmail(request.email())
        .orElseThrow(() -> new BusinessException(ErrorCode.LOGIN_USER_NOT_FOUND));

    if (request.newPassword().equals(user.getAuthInfo().getPassword())) {
      throw new BusinessException(ErrorCode.SAME_AS_OLD_PASSWORD);
    }

    user.changePassword(passwordEncoder.encode(request.newPassword()));
    userRepository.save(user);
  }
}
