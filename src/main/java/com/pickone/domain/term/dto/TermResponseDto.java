package com.pickone.domain.term.dto;

import java.time.LocalDateTime;

public record TermResponseDto(
    Long id,
    String title,
    String content,
    String version,
    boolean required,
    LocalDateTime effectiveDate
) {}
