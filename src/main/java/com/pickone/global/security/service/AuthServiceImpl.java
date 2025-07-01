package com.pickone.global.security.service;

import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.domain.user.repository.UserJpaRepository;
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
        .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));
    if (!passwordEncoder.matches(request.password(), user.getAuthInfo().getPassword())) {
      throw new RuntimeException("INVALID_PASSWORD");
    }
    // 반드시 UserPrincipal.from(user)로 Wrapping
    var principal = UserPrincipal.from(user);
    String accessToken = tokenProvider.generateAccessToken(principal);
    String refreshToken = tokenProvider.generateRefreshToken(principal);
    return new LoginResponse(accessToken, refreshToken, user.getId());
  }


  @Override
  public void logout(String refreshToken) {
    tokenProvider.blacklistToken(refreshToken);
  }

  @Override
  public void resetPassword(PasswordResetRequest request) {
    UserEntity user = userRepository.findByProfileEmail(request.email())
        .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));
    user.changePassword(passwordEncoder.encode(request.newPassword()));
    userRepository.save(user);
  }

}




