package com.pickone.global.email.dto;

public record EmailSendRequestDto(
    String to,
    String subject,
    String content
) {}
