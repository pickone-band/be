package com.pickone.domain.consent.service;

import com.pickone.domain.consent.dto.ConsentResponse;
import java.util.List;

public interface ConsentQueryService {
  List<ConsentResponse> getUserConsents(Long userId);
  boolean hasConsented(Long userId, Long termId);
}
