package com.PickOne.term.controller.dto.userConsent;

public record MarketingConsentRequest(boolean consented) {
    // For compatibility with isConsented() method name
    public boolean isConsented() {
        return consented;
    }
}