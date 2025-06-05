package com.PickOne.global.verification.dto;

public record VerificationResponse(
        boolean success,
        String message
) {}