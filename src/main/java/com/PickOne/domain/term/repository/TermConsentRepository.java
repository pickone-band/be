package com.PickOne.domain.term.repository;

import com.PickOne.domain.term.model.domain.TermConsent;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * UserConsent 도메인을 위한 기본 리포지토리 인터페이스
 */
@Repository
public interface TermConsentRepository {
    TermConsent save(TermConsent termsConsent);

    Optional<TermConsent> findByUserIdAndTermsId(Long userId, Long termsId);
    List<TermConsent> findAllByUserId(Long userId);
    boolean hasUserConsented(Long userId, Long termsId);
}