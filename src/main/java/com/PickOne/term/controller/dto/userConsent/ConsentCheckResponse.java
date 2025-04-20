package com.PickOne.term.controller.dto.userConsent;

public record ConsentCheckResponse(boolean consented) {
    // For compatibility with isConsented() method name
    public boolean isConsented() {
        return consented;
    }
}