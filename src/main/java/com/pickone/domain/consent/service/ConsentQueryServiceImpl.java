package com.pickone.domain.consent.service;

import com.pickone.domain.consent.dto.ConsentResponse;
import com.pickone.domain.consent.mapper.ConsentMapper;
import com.pickone.domain.consent.repository.ConsentJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConsentQueryServiceImpl implements ConsentQueryService {

  private final ConsentJpaRepository consentRepository;
  private final ConsentMapper consentMapper;

  @Override
  public List<ConsentResponse> getUserConsents(Long userId) {
    log.info("[ConsentQueryService] 사용자 약관 동의 목록 조회 요청: userId={}", userId);

    return consentRepository.findByUserId(userId).stream()
        .map(consentMapper::toDto)
        .toList();
  }

  @Override
  public boolean hasConsented(Long userId, Long termId) {
    log.info("[ConsentQueryService] 약관 동의 여부 확인 요청: userId={}, termId={}", userId, termId);

    boolean result = consentRepository.existsByUserIdAndTermIdAndConsentedTrue(userId, termId);

    log.info("[ConsentQueryService] 동의 여부 결과: userId={}, termId={}, consented={}", userId, termId, result);
    return result;
  }
}
