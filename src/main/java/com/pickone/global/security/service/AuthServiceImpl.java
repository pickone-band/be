package com.pickone.global.security.service;

import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.global.email.dto.EmailSendRequestDto;
import com.pickone.global.email.service.EmailSendService;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import com.pickone.global.security.dto.LoginRequest;
import com.pickone.global.security.dto.LoginResponse;
import com.pickone.global.security.model.entity.UserPrincipal;
import com.pickone.global.security.token.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final UserJpaRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final TokenProvider tokenProvider;
  private final EmailTokenService emailTokenService;
  private final EmailSendService emailSendService;

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

  public String validateTokenAndGetEmail(String token) {
    String email = emailTokenService.getEmailByToken(token);
    if (email == null) {
      throw new BusinessException(ErrorCode.INVALID_TOKEN);
    }
    return email;
  }

  @Transactional
  public void resetPasswordWithToken(String token, String newPassword) {
    if (newPassword == null || newPassword.isBlank()) {
      throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
    }

    String email = validateTokenAndGetEmail(token);

    UserEntity user = userRepository.findByProfileEmail(email)
        .orElseThrow(() -> new BusinessException(ErrorCode.LOGIN_USER_NOT_FOUND));

    if (passwordEncoder.matches(newPassword, user.getAuthInfo().getPassword())) {
      throw new BusinessException(ErrorCode.SAME_AS_OLD_PASSWORD);
    }

    user.changePassword(passwordEncoder.encode(newPassword));
    userRepository.save(user);

    emailTokenService.deleteToken(token);
  }

  @Override
  public void sendPasswordResetEmail(String email) {
    UserEntity user = userRepository.findByProfileEmail(email)
        .orElseThrow(() -> new BusinessException(ErrorCode.LOGIN_USER_NOT_FOUND));

    // 토큰 생성 및 저장
    String token = emailTokenService.createAndSaveToken(email);

    // 이메일 내용에 인증 코드(토큰)만 포함
    String subject = "[PickOne] 비밀번호 재설정 인증 코드";
    String content = "비밀번호 재설정을 위해 아래 인증 코드를 입력하세요:\n\n" + token + "\n\n코드는 24시간 동안 유효합니다.";

    EmailSendRequestDto emailRequest = new EmailSendRequestDto(email, subject, content);
    emailSendService.send(emailRequest);
  }
}
