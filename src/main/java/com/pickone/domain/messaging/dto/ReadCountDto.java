package com.pickone.domain.messaging.dto;

public record ReadCountDto(
    String messageId,
    long readCount,
    long totalCount
) {

}