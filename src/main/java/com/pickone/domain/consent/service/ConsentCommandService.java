package com.pickone.domain.consent.service;

import com.pickone.domain.consent.dto.ConsentRequestDto;
import com.pickone.domain.consent.dto.ConsentResponseDto;

public interface ConsentCommandService {
  ConsentResponseDto saveConsent(Long userId, ConsentRequestDto dto);
  void deleteConsent(Long userId, Long termId);
}
