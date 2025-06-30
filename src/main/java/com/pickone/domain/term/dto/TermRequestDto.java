package com.pickone.domain.term.dto;

import com.pickone.domain.term.model.entity.TermEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record TermRequestDto(
    @NotBlank String title,
    @NotBlank String content,
    @NotBlank String version,
    @NotNull Boolean required,
    @NotNull LocalDateTime effectiveDate
) {

  public TermEntity toEntity() {
    return TermEntity.create(title, content, version, required, effectiveDate);
  }
}
