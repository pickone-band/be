package com.pickone.domain.consent.policy;

import com.pickone.domain.consent.repository.ConsentJpaRepository;
import com.pickone.domain.term.model.entity.TermEntity;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsentPolicy {

  private final ConsentJpaRepository consentRepository;

  public boolean hasConsented(Long userId, Long termId) {
    return consentRepository.findByUserIdAndTermId(userId, termId)
        .map(consent -> consent.isConsented())
        .orElse(false);
  }

  public void validateRequiredConsent(Long userId, TermEntity term) {
    if (term.isRequired() && !hasConsented(userId, term.getId())) {
      throw new BusinessException(ErrorCode.REQUIRED_TERM_NOT_CONSENTED);
    }
  }
}
