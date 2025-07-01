package com.pickone.global.security.dto;

public record LoginRequest(
    String email,
    String password
) {}
