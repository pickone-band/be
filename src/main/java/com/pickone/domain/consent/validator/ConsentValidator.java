package com.pickone.domain.consent.validator;

import com.pickone.domain.consent.dto.ConsentRequest;
import com.pickone.domain.term.entity.Term;
import com.pickone.domain.term.repository.TermJpaRepository;
import com.pickone.domain.user.entity.User;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ConsentValidator {

  private final TermJpaRepository termRepository;

  public void validateSaveConsent(User user, Term term, boolean consented) {
    if (user == null || term == null) {
      throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
    }
  }

  public void validateRequiredTermsAgreed(User user, List<ConsentRequest> requests) {
    Set<Long> requiredTermIds = termRepository.findByIsRequiredTrue().stream()
        .map(Term::getId)
        .collect(Collectors.toSet());

    Map<Long, ConsentRequest> requestMap = requests.stream()
        .collect(Collectors.toMap(ConsentRequest::termId, Function.identity()));

    for (Long requiredId : requiredTermIds) {
      ConsentRequest dto = requestMap.get(requiredId);
      if (dto == null || !dto.consented()) {
        throw new BusinessException(ErrorCode.REQUIRED_TERM_NOT_AGREED);
      }
    }
  }
}
