package com.pickone.global.security.dto;

public record AuthResponseDto(String accessToken, String refreshToken, String email) {

  public static AuthResponseDto of(AuthResult result) {
    return new AuthResponseDto(
        result.accessToken(),
        result.refreshToken(),
        result.email())
        ;
  }
}
