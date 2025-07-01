package com.pickone.domain.consent.service;

import com.pickone.domain.consent.dto.ConsentResponseDto;
import com.pickone.domain.consent.model.mapper.ConsentMapper;
import com.pickone.domain.consent.repository.ConsentJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsentQueryServiceImpl implements ConsentQueryService {
  private final ConsentJpaRepository consentRepository;

  @Override
  public List<ConsentResponseDto> getUserConsents(Long userId) {
    return consentRepository.findByUserId(userId).stream()
        .map(ConsentMapper::toDto)
        .toList();
  }

  @Override
  public boolean hasConsented(Long userId, Long termId) {
    return consentRepository.existsByUserIdAndTermIdAndConsentedTrue(userId, termId);
  }
}
