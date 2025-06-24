package com.pickone.global.security.service;

import com.pickone.global.security.dto.AuthResult;
import com.pickone.global.security.dto.ChangePasswordRequest;
import com.pickone.global.security.dto.LoginRequest;
import com.pickone.global.security.dto.SignupRequestDto;

public interface AuthService {
    AuthResult signup(SignupRequestDto request);
    AuthResult login(LoginRequest request);
    AuthResult refresh(String refreshToken);
    void logout(String accessToken);
    void changePassword(String accessToken, ChangePasswordRequest request);

}