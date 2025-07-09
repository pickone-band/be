package com.pickone.global.security.service;

import com.pickone.domain.user.entity.User;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.global.email.service.EmailSendService;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import com.pickone.global.security.dto.LoginRequest;
import com.pickone.global.security.dto.LoginResponse;
import com.pickone.global.security.entity.UserPrincipal;
import com.pickone.global.security.token.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

  private final UserJpaRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final TokenProvider tokenProvider;
  private final EmailTokenService emailTokenService;
  private final EmailSendService emailSendService;

  @Override
  public LoginResponse login(LoginRequest request) {
    log.info("[AuthService] 로그인 요청: email={}", request.email());
    User user = userRepository.findByProfileEmail(request.email())
        .orElseThrow(() -> {
          log.warn("[AuthService] 사용자 없음: {}", request.email());
          throw new BusinessException(ErrorCode.LOGIN_USER_NOT_FOUND);
        });

    if (!passwordEncoder.matches(request.password(), user.getAuthInfo().getPassword())) {
      log.warn("[AuthService] 비밀번호 불일치: email={}", request.email());
      throw new BusinessException(ErrorCode.INVALID_PASSWORD_CREDENTIAL);
    }

    if (!user.getStatus().isVerified()) {
      log.warn("[AuthService] 이메일 미인증 사용자: email={}", request.email());
      throw new BusinessException(ErrorCode.EMAIL_NOT_VERIFIED);
    }

    UserPrincipal principal = UserPrincipal.from(user);
    String accessToken = tokenProvider.generateAccessToken(principal);
    String refreshToken = tokenProvider.generateRefreshToken(principal);

    log.info("[AuthService] 로그인 성공: userId={}, email={}", user.getId(), user.getProfile().getEmail());

    return new LoginResponse(accessToken, refreshToken, user.getId());
  }

  @Override
  public void logout(String refreshToken) {
    log.info("[AuthService] 로그아웃 요청");
    if (refreshToken == null || refreshToken.isBlank()) {
      log.warn("[AuthService] 리프레시 토큰 없음");
      throw new BusinessException(ErrorCode.TOKEN_NOT_FOUND);
    }

    tokenProvider.blacklistToken(refreshToken);

    log.info("[AuthService] 로그아웃 완료: 토큰 블랙리스트 등록 완료");
  }

  @Override
  public void sendPasswordResetEmail(String email) {
    log.info("[AuthService] 비밀번호 재설정 이메일 요청: email={}", email);
    User user = userRepository.findByProfileEmail(email)
        .orElseThrow(() -> {
          log.warn("[AuthService] 사용자 없음: {}", email);
          throw new BusinessException(ErrorCode.LOGIN_USER_NOT_FOUND);
        });

    String token = emailTokenService.createAndSaveToken(email);

    emailSendService.sendPasswordResetEmail(email, token);

    log.info("[AuthService] 비밀번호 재설정 이메일 전송 완료: email={}", email);
  }

  @Override
  @Transactional
  public void resetPasswordWithToken(String token, String newPassword) {
    log.info("[AuthService] 비밀번호 재설정 시작: token={}", token);
    String email = emailTokenService.validateTokenAndGetEmail(token)
        .orElseThrow(() -> {
          log.warn("[AuthService] 유효하지 않은 토큰");
          throw new BusinessException(ErrorCode.INVALID_TOKEN);
        });

    User user = userRepository.findByProfileEmail(email)
        .orElseThrow(() -> {
          log.warn("[AuthService] 사용자 없음: {}", email);
          throw new BusinessException(ErrorCode.LOGIN_USER_NOT_FOUND);
        });

    if (passwordEncoder.matches(newPassword, user.getAuthInfo().getPassword())) {
      log.warn("[AuthService] 기존 비밀번호와 동일: email={}", email);
      throw new BusinessException(ErrorCode.SAME_AS_OLD_PASSWORD);
    }

    user.changePassword(passwordEncoder.encode(newPassword));
    userRepository.save(user);
    emailTokenService.deleteToken(token);

    log.info("[AuthService] 비밀번호 재설정 완료: email={}", email);
  }
}