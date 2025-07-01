package com.pickone.global.security.service;

import com.pickone.global.security.dto.LoginRequest;
import com.pickone.global.security.dto.LoginResponse;
import com.pickone.global.security.dto.PasswordResetRequest;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    void logout(String refreshToken);
    void resetPassword(PasswordResetRequest request);
}
