package com.pickone.domain.consent.service;

import com.pickone.domain.consent.dto.ConsentRequest;
import com.pickone.domain.consent.dto.ConsentResponse;

public interface ConsentCommandService {
  ConsentResponse saveConsent(Long userId, ConsentRequest dto);
  void deleteConsent(Long userId, Long termId);
}
