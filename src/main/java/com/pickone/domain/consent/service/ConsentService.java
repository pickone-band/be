package com.pickone.domain.consent.service;

import com.pickone.domain.consent.dto.request.ConsentRequestDto;
import com.pickone.domain.consent.model.entity.ConsentEntity;
import com.pickone.domain.consent.repository.ConsentJpaRepository;
import com.pickone.domain.term.model.entity.TermEntity;
import com.pickone.domain.term.repository.TermJpaRepository;
import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsentService {

  private final ConsentJpaRepository consentRepository;
  private final UserJpaRepository userRepository;
  private final TermJpaRepository termRepository;

  @Transactional
  public ConsentEntity saveConsent(Long userId, Long termId, ConsentRequestDto requestDto) {
    UserEntity user = userRepository.findById(userId)
        .orElseThrow(() -> {
          log.warn("사용자 없음: userId={}", userId);
          return new BusinessException(ErrorCode.USER_INFO_NOT_FOUND);
        });

    TermEntity term = termRepository.findById(termId)
        .orElseThrow(() -> {
          log.warn("약관 없음: termId={}", termId);
          return new BusinessException(ErrorCode.TERM_NOT_FOUND);
        });

    ConsentEntity entity = requestDto.toEntity(user, term);
    log.info("약관 동의 저장: userId={}, termId={}, consented={}", userId, termId, entity.isConsented());
    return consentRepository.save(entity);
  }


  @Transactional(readOnly = true)
  public List<ConsentEntity> getUserConsents(Long userId) {
    log.debug("사용자 약관 동의 전체 조회: userId={}", userId);
    return consentRepository.findByUserId(userId);
  }


  @Transactional(readOnly = true)
  public boolean hasConsented(Long userId, Long termId) {
    boolean result = consentRepository.findByUserIdAndTermId(userId, termId)
        .map(ConsentEntity::isConsented)
        .orElse(false);
    log.info("동의 여부 확인: userId={}, termId={}, result={}", userId, termId, result);
    return result;
  }


  @Transactional
  public void deleteConsent(Long userId, Long termId) {
    log.info("약관 동의 삭제: userId={}, termId={}", userId, termId);
    consentRepository.deleteByUserIdAndTermId(userId, termId);
  }


}
