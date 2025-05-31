package com.PickOne.domain.term.dto;

import com.PickOne.domain.term.model.domain.Term;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * 약관 등록 요청 DTO
 */
public record TermRequestDto(
        @NotBlank String title,
        @NotBlank String content,
        @NotBlank String version,
        @NotNull Boolean required,
        @NotNull LocalDateTime effectiveDate
) {
    public Term toDomain(Long id) {
        return new Term(id, title, content, version, required, effectiveDate);
    }
}