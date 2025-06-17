package com.PickOne.global.security.service;

import com.PickOne.global.security.dto.AuthResult;
import com.PickOne.global.security.dto.ChangePasswordRequest;
import com.PickOne.global.security.dto.LoginRequest;
import com.PickOne.global.security.dto.SignupRequestDto;

public interface AuthService {
    AuthResult signup(SignupRequestDto request);
    AuthResult login(LoginRequest request);
    AuthResult refresh(String refreshToken);
    void logout(String accessToken);
    void changePassword(String accessToken, ChangePasswordRequest request);

}