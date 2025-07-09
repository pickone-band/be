package com.pickone.domain.consent.service;

import com.pickone.domain.consent.dto.ConsentRequest;
import com.pickone.domain.consent.dto.ConsentResponse;
import com.pickone.domain.consent.entity.Consent;
import com.pickone.domain.consent.mapper.ConsentMapper;
import com.pickone.domain.consent.repository.ConsentJpaRepository;
import com.pickone.domain.consent.validator.ConsentValidator;
import com.pickone.domain.term.entity.Term;
import com.pickone.domain.term.repository.TermJpaRepository;
import com.pickone.domain.term.validator.TermValidator;
import com.pickone.domain.user.entity.User;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConsentCommandServiceImpl implements ConsentCommandService {

  private final UserJpaRepository userRepository;
  private final TermJpaRepository termRepository;
  private final ConsentJpaRepository consentRepository;
  private final ConsentValidator validator;
  private final ConsentMapper consentMapper;

  @Override
  @Transactional
  public ConsentResponse saveConsent(Long userId, ConsentRequest dto) {
    log.info("[ConsentCommandService] 약관 동의 저장 요청: userId={}, termId={}, consented={}",
        userId, dto.termId(), dto.consented());

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));
    Term term = termRepository.findById(dto.termId())
        .orElseThrow(() -> new BusinessException(ErrorCode.TERM_NOT_FOUND));

    validator.validateSaveConsent(user, term, dto.consented());

    Consent entity = Consent.create(user, term, dto.consented(), LocalDateTime.now());
    Consent saved = consentRepository.save(entity);

    log.info("[ConsentCommandService] 약관 동의 저장 완료: consentId={}, userId={}, termId={}",
        saved.getId(), userId, term.getId());

    return consentMapper.toDto(saved);
  }

  @Override
  @Transactional
  public void deleteConsent(Long userId, Long termId) {
    log.info("[ConsentCommandService] 약관 동의 삭제 요청: userId={}, termId={}", userId, termId);

    Consent consent = consentRepository.findByUserIdAndTermId(userId, termId)
        .orElseThrow(() -> new BusinessException(ErrorCode.CONSENT_NOT_FOUND));

    consentRepository.delete(consent);

    log.info("[ConsentCommandService] 약관 동의 삭제 완료: consentId={}, userId={}, termId={}",
        consent.getId(), userId, termId);
  }
}
