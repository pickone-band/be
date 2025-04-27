package com.PickOne.term.controller.dto.userConsent;

public record ConsentRequest(Long termsId, boolean consented) {
    public boolean isConsented() {
        return consented;
    }
}