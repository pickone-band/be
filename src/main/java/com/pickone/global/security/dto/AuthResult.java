package com.pickone.global.security.dto;

public record AuthResult(String accessToken, String refreshToken, String email) {

}
