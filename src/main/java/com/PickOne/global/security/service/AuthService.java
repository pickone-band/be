package com.PickOne.global.security.service;

import com.PickOne.global.security.dto.AuthResult;
import com.PickOne.global.security.dto.LoginRequest;
import com.PickOne.global.security.dto.SignupRequest;

public interface AuthService {
    AuthResult signup(SignupRequest request);
    AuthResult login(LoginRequest request);
    AuthResult refresh(String refreshToken);
    void logout(String accessToken);
}