package com.pickone.global.security.controller;

import com.pickone.global.security.dto.*;
import com.pickone.global.security.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthService authService;

  @PostMapping("/login")
  public LoginResponse login(@RequestBody LoginRequest request) {
    return authService.login(request);
  }

  @PostMapping("/logout")
  public void logout(@RequestParam String refreshToken) {
    authService.logout(refreshToken);
  }

  @PostMapping("/password-reset")
  public void resetPassword(@RequestBody PasswordResetRequest request) {
    authService.resetPassword(request);
  }
}
