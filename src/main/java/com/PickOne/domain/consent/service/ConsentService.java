package com.PickOne.domain.consent.service;

import com.PickOne.domain.consent.model.domain.Consent;
import com.PickOne.domain.consent.repository.ConsentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsentService {

    private final ConsentRepository consentRepository;

    @Transactional
    public Consent saveConsent(Consent consent) {
        return consentRepository.save(consent);
    }

    @Transactional(readOnly = true)
    public List<Consent> getUserConsents(Long userId) {
        return consentRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public boolean hasConsented(Long userId, Long termsId) {
        return consentRepository.findByUserIdAndTermsId(userId, termsId)
                .map(Consent::isConsented)
                .orElse(false);
    }

    @Transactional
    public void deleteConsent(Long userId, Long termsId) {
        consentRepository.deleteByUserIdAndTermsId(userId, termsId);
    }
}