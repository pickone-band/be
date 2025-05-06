package com.PickOne.global.security.service;

import com.PickOne.domain.user.model.domain.User;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {

    User signup(String email, String password);
    User login(String email, String password);
    User refreshToken(String refreshToken);
    void logout(HttpServletRequest request);
    String generateAccessToken(User user);
    String generateRefreshToken(User user);
}