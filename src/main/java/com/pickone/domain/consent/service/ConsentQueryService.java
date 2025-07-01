package com.pickone.domain.consent.service;

import com.pickone.domain.consent.dto.ConsentResponseDto;

import java.util.List;

public interface ConsentQueryService {
  List<ConsentResponseDto> getUserConsents(Long userId);
  boolean hasConsented(Long userId, Long termId);
}
