package com.pickone.domain.term.mapper;

import com.pickone.domain.term.dto.TermRequest;
import com.pickone.domain.term.dto.TermResponse;
import com.pickone.domain.term.entity.Term;
import org.springframework.stereotype.Component;

@Component
public class TermMapper {

  public Term toEntity(TermRequest dto) {
    if (dto == null) return null;

    return Term.create(
        dto.title(),
        dto.content(),
        dto.version(),
        dto.required(),
        dto.effectiveDate()
    );
  }

  public TermResponse toDto(Term entity) {
    if (entity == null) return null;

    return new TermResponse(
        entity.getId(),
        entity.getTitle(),
        entity.getContent(),
        entity.getVersion(),
        entity.isRequired(),
        entity.getEffectiveDate()
    );
  }
}
