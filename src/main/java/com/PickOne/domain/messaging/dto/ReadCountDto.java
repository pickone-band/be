package com.PickOne.domain.messaging.dto;

public record ReadCountDto(
        String messageId,
        long readCount,
        long totalCount
) {}