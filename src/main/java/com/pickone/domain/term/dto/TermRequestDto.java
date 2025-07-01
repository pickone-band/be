package com.pickone.domain.term.dto;

import java.time.LocalDateTime;

public record TermRequestDto(
    String title,
    String content,
    String version,
    boolean required,
    LocalDateTime effectiveDate
) {}
