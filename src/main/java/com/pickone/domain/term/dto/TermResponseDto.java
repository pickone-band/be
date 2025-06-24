package com.pickone.domain.term.dto;

import com.pickone.domain.term.model.entity.TermEntity;

import java.time.LocalDateTime;

public record TermResponseDto(Long id, String title, String content, String version,
                              boolean required,
                              LocalDateTime effectiveDate) {

  public static TermResponseDto fromEntity(TermEntity term) {
    return new TermResponseDto(term.getId(), term.getTitle(), term.getContent(), term.getVersion(),
        term.isRequired(), term.getEffectiveDate());
  }
}
