package com.pickone.domain.term.validator;

import com.pickone.domain.term.dto.TermRequest;
import com.pickone.domain.term.entity.Term;
import com.pickone.domain.term.repository.TermJpaRepository;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class TermValidator {

  private final TermJpaRepository termRepository;

  public void validateCreate(TermRequest dto) {
    if (dto.title() == null || dto.title().isBlank()) {
      throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
    }
    if (dto.version() == null || dto.version().isBlank()) {
      throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
    }
    if (termRepository.existsByTitleAndVersion(dto.title(), dto.version())) {
      throw new BusinessException(ErrorCode.DUPLICATE_TERM_VERSION);
    }
    if (dto.effectiveDate().isBefore(LocalDateTime.now())) {
      throw new BusinessException(ErrorCode.INVALID_DATE);
    }
  }

  public void validateUpdate(Term term, TermRequest dto) {
    if (!term.getVersion().equals(dto.version())) {
      throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
    }
  }
}
