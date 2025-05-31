package com.PickOne.domain.consent.repository;

import com.PickOne.domain.consent.model.domain.Consent;

import java.util.List;
import java.util.Optional;

public interface ConsentRepository {
    Consent save(Consent consent);
    Optional<Consent> findByUserIdAndTermsId(Long userId, Long termsId);
    List<Consent> findByUserId(Long userId);
    void deleteByUserIdAndTermsId(Long userId, Long termsId);
}
