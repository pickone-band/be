package com.pickone.domain.consent.service;

import com.pickone.domain.consent.dto.ConsentTermtDto;
import com.pickone.domain.consent.model.entity.ConsentEntity;
import com.pickone.domain.consent.policy.ConsentPolicy;
import com.pickone.domain.consent.repository.ConsentJpaRepository;

import com.pickone.domain.term.model.entity.TermEntity;
import com.pickone.domain.term.repository.TermJpaRepository;
import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConsentService {

  private final UserJpaRepository userRepository;
  private final ConsentJpaRepository consentRepository;
  private final TermJpaRepository termRepository;
  private final ConsentPolicy consentPolicy;

  @Transactional
  public ConsentEntity saveConsent(Long userId, Long termId, Boolean consented) {
    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));

    TermEntity term = termRepository.findById(termId)
        .orElseThrow(() -> new BusinessException(ErrorCode.TERM_NOT_FOUND));

    ConsentEntity consent = new ConsentEntity(
        null,
        user,
        term,
        consented,
        LocalDateTime.now()
    );

    return consentRepository.save(consent);
  }

  @Transactional
  public void saveAll(UserEntity user, List<ConsentTermtDto> dtoList) {
    for (ConsentTermtDto dto : dtoList) {
      TermEntity term = termRepository.findById(dto.termId())
          .orElseThrow(() -> new BusinessException(ErrorCode.TERM_NOT_FOUND));

      ConsentEntity consent = new ConsentEntity(
          null,
          user,
          term,
          dto.consented(),
          LocalDateTime.now()
      );

      consentRepository.save(consent);
    }
  }

  public List<ConsentEntity> getUserConsents(Long userId) {
    return consentRepository.findByUserId(userId);
  }

  public boolean hasConsented(Long userId, Long termId) {
    return consentPolicy.hasConsented(userId, termId);
  }

  @Transactional
  public void deleteConsent(Long userId, Long termId) {
    ConsentEntity entity = consentRepository.findByUserIdAndTermId(userId, termId)
        .orElseThrow(() -> new BusinessException(ErrorCode.CONSENT_NOT_FOUND));

    consentRepository.delete(entity);
  }
}
