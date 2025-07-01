package com.pickone.global.security.dto;

public record PasswordResetRequest(
    String email,
    String newPassword
) {}
