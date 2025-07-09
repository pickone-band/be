package com.pickone.domain.consent.repository;

import java.util.List;

public interface ConsentQueryRepository {
  List<Long> findConsentedTermIdsByUserId(Long userId);
}