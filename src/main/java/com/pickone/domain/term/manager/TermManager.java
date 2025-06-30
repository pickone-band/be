package com.pickone.domain.term.manager;

import com.pickone.domain.term.model.entity.TermEntity;
import com.pickone.domain.term.repository.TermJpaRepository;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class TermManager {

  private final TermJpaRepository termRepository;

  public TermEntity register(String title, String content, String version, boolean required, LocalDateTime effectiveDate) {
    boolean exists = termRepository.existsByTitleAndVersion(title, version);
    if (exists) {
      throw new BusinessException(ErrorCode.TERM_ALREADY_EXISTS);
    }

    TermEntity newTerm = TermEntity.create(
        title,
        content,
        version,
        required,
        effectiveDate
    );

    return termRepository.save(newTerm);
  }

  private void validateDuplication(String title, String version) {
    boolean exists = termRepository.existsByTitleAndVersion(title, version);
    if (exists) {
      throw new BusinessException(ErrorCode.TERM_ALREADY_EXISTS);
    }
  }
}
