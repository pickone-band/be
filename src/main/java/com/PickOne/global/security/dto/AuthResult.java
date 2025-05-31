package com.PickOne.global.security.dto;

import com.PickOne.domain.user.model.domain.User;

public record AuthResult(String accessToken, String refreshToken, User user) {}
