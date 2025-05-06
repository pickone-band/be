package com.PickOne.domain.term.model.domain;

import lombok.Getter;

import java.util.Arrays;

/**
 * 약관 유형을 나타내는 열거형
 */
@Getter
public enum TermsType {

    SERVICE("service", "서비스 이용약관"),
    PRIVACY("privacy", "개인정보 처리방침"),
    MARKETING("marketing", "마케팅 정보 수신 동의");

    private final String value;
    private final String displayName;

    TermsType(String value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    /**
     * 문자열 값으로부터 TermsType을 찾습니다.
     *
     * @param text 검색할 유형 문자열
     * @return 해당하는 TermsType
     * @throws IllegalArgumentException 일치하는 유형이 없을 경우
     */
    public static TermsType fromString(String text) {
        return Arrays.stream(TermsType.values())
                .filter(type -> type.value.equalsIgnoreCase(text))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "유효하지 않은 약관 유형입니다: " + text));
    }

    /**
     * 해당 약관이 필수 동의 약관인지 확인합니다.
     *
     * @return 필수 동의 약관일 경우 true
     */
    public boolean isRequiredConsent() {
        return this == SERVICE || this == PRIVACY;
    }

    /**
     * 마케팅 약관인지 확인합니다.
     *
     * @return 마케팅 약관일 경우 true
     */
    public boolean isMarketingConsent() {
        return this == MARKETING;
    }
}