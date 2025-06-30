package com.pickone.global.security.dto;

public record AuthResult(String accessToken, String refreshToken, String email) {
  public static AuthResult from(String accessToken, String refreshToken, String email) {
    return new AuthResult(accessToken, refreshToken, email);
  }
}
