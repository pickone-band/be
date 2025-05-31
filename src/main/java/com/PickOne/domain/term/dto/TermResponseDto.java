package com.PickOne.domain.term.dto;

import com.PickOne.domain.term.model.domain.Term;

import java.time.LocalDateTime;

/**
 * 약관 조회 응답 DTO
 */
public record TermResponseDto(
        Long id,
        String title,
        String content,
        String version,
        boolean required,
        LocalDateTime effectiveDate
) {
    public static TermResponseDto from(Term term) {
        return new TermResponseDto(
                term.getId(),
                term.getTitle(),
                term.getContent(),
                term.getVersion(),
                term.isRequired(),
                term.getEffectiveDate()
        );
    }
}
