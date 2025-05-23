package com.PickOne.global.auth.dto;

public record VerificationResponse(
        boolean success,
        String message
) {}