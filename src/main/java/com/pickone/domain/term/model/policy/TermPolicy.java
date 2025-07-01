package com.pickone.domain.term.model.policy;

import com.pickone.domain.term.dto.TermRequestDto;
import com.pickone.domain.term.model.entity.TermEntity;
import com.pickone.domain.term.repository.TermJpaRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TermPolicy {

  private final TermJpaRepository termRepository;

  public void validateCreate(TermRequestDto dto) {
    if (dto.title() == null || dto.title().isBlank()) {
      throw new IllegalArgumentException("title is required");
    }
    if (dto.content() == null || dto.content().isBlank()) {
      throw new IllegalArgumentException("content is required");
    }
    if (dto.version() == null || dto.version().isBlank()) {
      throw new IllegalArgumentException("version is required");
    }
    if (dto.effectiveDate() == null) {
      throw new IllegalArgumentException("effectiveDate is required");
    }
  }
  public List<TermEntity> getRequiredTerms() {
    return termRepository.findByIsRequiredTrue();
  }
}


