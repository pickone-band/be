package com.PickOne.global.security.service;

import com.PickOne.domain.user.mapper.UserMapper;
import com.PickOne.domain.user.model.domain.*;
import com.PickOne.domain.user.repository.UserRepository;

import com.PickOne.global.security.dto.LoginRequest;
import com.PickOne.global.security.dto.SignupRequest;
import com.PickOne.global.security.dto.AuthResponseDto;
import com.PickOne.global.security.dto.AuthResult;

import com.PickOne.global.security.model.entity.UserPrincipal;
import com.PickOne.global.security.repository.AuthRepository;
import com.PickOne.global.security.repository.RefreshTokenRepository;
import com.PickOne.global.security.repository.TokenBlacklistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final AuthRepository authRepository;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final RefreshTokenRepository refreshTokenRepository;
  private final TokenBlacklistRepository tokenBlacklistRepository;

  @Override
  public AuthResult signup(SignupRequest request) {
    Email email = Email.of(request.email());
    if (userRepository.findByEmail(email).isPresent()) {
      throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
    }
    Password password = Password.ofRaw(request.password(), passwordEncoder);

    User user = new User(
            null,
            email,
            password,
            null,
            null,
            true,
            false,
            false,
            Role.USER,
            List.of(),
            List.of()
    );
    user = authRepository.save(user);
    return issueTokens(user);
  }

  @Override
  public AuthResult login(LoginRequest request) {
    User user = userRepository.findByEmail(Email.of(request.email()))
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이메일입니다."));

    if (!user.getPassword().matches(request.password(), passwordEncoder)) {
      throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
    }

    return issueTokens(user);
  }

  @Override
  public AuthResult refresh(String refreshToken) {
    if (!jwtService.validateRefreshToken(refreshToken)) {
      throw new IllegalArgumentException("유효하지 않은 리프레시 토큰입니다.");
    }

    Email email = Email.of(jwtService.extractUsername(refreshToken));
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

    return issueTokens(user);
  }

  @Override
  public void logout(String accessToken) {
    jwtService.blacklistToken(accessToken);
  }

  private AuthResult issueTokens(User user) {
    String accessToken = jwtService.generateAccessToken(UserPrincipal.from(user));
    String refreshToken = jwtService.generateRefreshToken(UserPrincipal.from(user));
    refreshTokenRepository.save(user.getEmail().getValue(), refreshToken, jwtService.getRefreshTokenExpiration());

    return new AuthResult(accessToken, refreshToken, user);
  }
}
