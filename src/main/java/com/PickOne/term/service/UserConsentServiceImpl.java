package com.PickOne.term.service;

import com.PickOne.term.model.domain.TermConsent;
import com.PickOne.term.repository.TermConsentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserConsentServiceImpl implements UserConsentService {

    private final TermConsentRepository termConsentRepository;

    @Override
    @Transactional
    public TermConsent saveConsent(TermConsent consent) {
        return termConsentRepository.save(consent);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasUserConsented(Long userId, Long termsId) {
        return termConsentRepository.hasUserConsented(userId, termsId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TermConsent> getUserConsents(Long userId) {
        return termConsentRepository.findAllByUserId(userId);
    }
}