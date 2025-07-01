package com.pickone.domain.term.model.factory;

import com.pickone.domain.term.dto.TermRequestDto;
import com.pickone.domain.term.model.entity.TermEntity;
import org.springframework.stereotype.Component;

@Component
public class TermFactory {
  public TermEntity create(TermRequestDto dto) {
    return TermEntity.create(
        dto.title(),
        dto.content(),
        dto.version(),
        dto.required(),
        dto.effectiveDate()
    );
  }
}
