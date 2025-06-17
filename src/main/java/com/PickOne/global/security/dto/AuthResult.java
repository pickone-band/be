package com.PickOne.global.security.dto;

public record AuthResult(String accessToken, String refreshToken, String email) {}
