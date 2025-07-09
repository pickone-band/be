package com.pickone.global.security.mapper;

import com.pickone.domain.user.entity.User;
import com.pickone.global.security.dto.*;

import org.springframework.stereotype.Component;

@Component
public class AuthMapper {

  /**
   * accessToken, refreshToken, email로 AuthResult 생성
   */
  public AuthResult toAuthResult(String accessToken, String refreshToken, String email) {
    return new AuthResult(accessToken, refreshToken, email);
  }

  /**
   * AuthResult → AuthResponse 변환
   */
  public AuthResponse toAuthResponse(AuthResult result) {
    if (result == null) return null;
    return new AuthResponse(
        result.accessToken(),
        result.refreshToken(),
        result.email()
    );
  }

  /**
   * User + access/refresh 토큰 → LoginResponse 생성
   */
  public LoginResponse toLoginResponse(User user, String accessToken, String refreshToken) {
    if (user == null) return null;
    return new LoginResponse(
        accessToken,
        refreshToken,
        user.getId()
    );
  }

  /**
   * 단순 래핑된 AuthResponse 생성자 활용
   */
  public AuthResponse toAuthResponse(String accessToken, String refreshToken, String email) {
    return new AuthResponse(accessToken, refreshToken, email);
  }
}
