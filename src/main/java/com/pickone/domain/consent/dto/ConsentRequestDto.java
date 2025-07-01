package com.pickone.domain.consent.dto;

public record ConsentRequestDto(
    Long termId,
    Boolean consented
) {}
