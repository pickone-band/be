package com.PickOne.term.service;

import com.PickOne.term.model.domain.TermConsent;

import java.util.List;

public interface UserConsentService {
    TermConsent saveConsent(TermConsent consent);
    boolean hasUserConsented(Long userId, Long termsId);
    List<TermConsent> getUserConsents(Long userId);
}