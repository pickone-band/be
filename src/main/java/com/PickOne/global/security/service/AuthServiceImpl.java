// AuthServiceImpl.java
package com.PickOne.global.security.service;

import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
import com.PickOne.domain.user.model.domain.Email;
import com.PickOne.domain.user.model.domain.Password;
import com.PickOne.domain.user.model.domain.User;
import com.PickOne.domain.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  @Override
  @Transactional
  public User signup(String emailValue, String passwordValue) {
    // 이메일 객체 생성
    Email email = Email.of(emailValue);

    // 이메일 중복 확인
    userRepository
        .findByEmail(emailValue)
        .ifPresent(
            user -> {
              throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
            });

    // 비밀번호 암호화
    String encodedPassword = passwordEncoder.encode(passwordValue);
    Password password = Password.ofEncoded(encodedPassword);

    // 사용자 생성
    User user = User.create(email, password);
    return userRepository.save(user);
  }

  @Override
  @Transactional(readOnly = true)
  public User login(String emailValue, String passwordValue) {
    // 사용자 조회
    User user =
        userRepository
            .findByEmail(emailValue)
            .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_PASSWORD));

    // 비밀번호 검증
    if (!passwordEncoder.matches(passwordValue, user.getPasswordValue())) {
      throw new BusinessException(ErrorCode.INVALID_PASSWORD);
    }

    return user;
  }

  @Override
  @Transactional(readOnly = true)
  public User refreshToken(String refreshToken) {
    // 리프레시 토큰 검증
    if (!jwtService.validateRefreshToken(refreshToken)) {
      throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
    }

    // 사용자 정보 추출
    Long userId = jwtService.getUserIdFromToken(refreshToken);
    return userRepository
        .findById(userId)
        .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));
  }

  @Override
  @Transactional
  public void logout(HttpServletRequest request) {
    String token = jwtService.resolveToken(request);
    if (token != null) {
      jwtService.blacklistToken(token);
    }
  }

  @Override
  public String generateAccessToken(User user) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("userId", user.getId());

    // Add minimal authorities needed for token
    List<String> authorities = getAuthoritiesFromUser(user);
    claims.put("authorities", authorities);

    return jwtService.generateToken(claims, user.getEmailValue(), jwtService.getAccessTokenExpiration());
  }

  @Override
  public String generateRefreshToken(User user) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("userId", user.getId());

    return jwtService.generateToken(claims, user.getEmailValue(), jwtService.getRefreshTokenExpiration());
  }

    private List<String> getAuthoritiesFromUser(User user) {
        // Since we don't want to directly use CustomUserDetailsService here (to avoid circular dependency),
        // we provide a minimal implementation for token generation purposes.
        // This can be extended if you need to include specific authorities in the token.
        return Collections.emptyList();
    }
}