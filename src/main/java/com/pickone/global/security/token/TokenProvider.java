package com.pickone.global.security.token;

import com.pickone.global.security.entity.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;

public interface TokenProvider {

  String generateAccessToken(UserPrincipal userDetails);

  String generateRefreshToken(UserPrincipal userDetails);

  long getAccessTokenExpiration();

  long getRefreshTokenExpiration();

  Authentication getAuthentication(String token);

  boolean validateRefreshToken(String refreshToken);

  String extractUsername(String token);

  String resolveToken(HttpServletRequest request);

  void blacklistToken(String token);

  boolean isTokenBlacklisted(String token);

  Long getUserIdFromToken(String token);
}
