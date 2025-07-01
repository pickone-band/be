package com.pickone.domain.term.model.mapper;

import com.pickone.domain.term.dto.TermRequestDto;
import com.pickone.domain.term.dto.TermResponseDto;
import com.pickone.domain.term.model.entity.TermEntity;

public class TermMapper {
  public static TermEntity toEntity(TermRequestDto dto) {
    return TermEntity.create(
        dto.title(),
        dto.content(),
        dto.version(),
        dto.required(),
        dto.effectiveDate()
    );
  }

  public static TermResponseDto toDto(TermEntity entity) {
    return new TermResponseDto(
        entity.getId(),
        entity.getTitle(),
        entity.getContent(),
        entity.getVersion(),
        entity.isRequired(),
        entity.getEffectiveDate()
    );
  }
}
