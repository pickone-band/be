package com.PickOne.global.verification.dto;

public record EmailMessage(
        String to,
        String subject,
        String body,
        boolean isHtml
) {}