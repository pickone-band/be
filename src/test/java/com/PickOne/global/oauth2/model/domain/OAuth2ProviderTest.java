package com.PickOne.global.oauth2.model.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OAuth2ProviderTest {

    @Test
    @DisplayName("OAuth2Provider 열거형 값 확인")
    void enumValues() {
        // when, then
        assertEquals("Google", OAuth2Provider.GOOGLE.getValue());
    }

    @Test
    @DisplayName("valueOf 메서드 테스트")
    void valueOf() {
        // when
        OAuth2Provider provider = OAuth2Provider.valueOf("GOOGLE");

        // then
        assertEquals(OAuth2Provider.GOOGLE, provider);
        assertThrows(IllegalArgumentException.class, () -> {
            OAuth2Provider.valueOf("INVALID");
        });
    }
}