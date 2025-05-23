package com.PickOne.global.auth.model.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class VerificationTokenTest {

    @Test
    void testCreateEmailVerificationToken() {
        VerificationToken token = VerificationToken.createEmailVerificationToken(1L);

        assertNotNull(token.getToken());
        assertEquals(1L, token.getUserId());
        assertEquals(VerificationToken.TokenType.EMAIL_VERIFICATION, token.getTokenType());
        assertFalse(token.isExpired());
    }

    @Test
    void testCreatePasswordResetToken() {
        VerificationToken token = VerificationToken.createPasswordResetToken(2L);

        assertNotNull(token.getToken());
        assertEquals(2L, token.getUserId());
        assertEquals(VerificationToken.TokenType.PASSWORD_RESET, token.getTokenType());
        assertFalse(token.isExpired());
    }

    @Test
    void testIsExpired() {
        VerificationToken token = new VerificationToken(
                "token123", 1L, VerificationToken.TokenType.EMAIL_VERIFICATION,
                LocalDateTime.now().minusHours(25),
                LocalDateTime.now().minusHours(1)
        );

        assertTrue(token.isExpired());
    }

    @Test
    void testEqualsAndHashCodeBasedOnToken() {
        String tokenStr = "same-token";
        VerificationToken token1 = new VerificationToken(
                tokenStr, 1L, VerificationToken.TokenType.EMAIL_VERIFICATION,
                LocalDateTime.now(), LocalDateTime.now().plusHours(24)
        );

        VerificationToken token2 = new VerificationToken(
                tokenStr, 2L, VerificationToken.TokenType.PASSWORD_RESET,
                LocalDateTime.now(), LocalDateTime.now().plusHours(1)
        );

        assertEquals(token1, token2);
        assertEquals(token1.hashCode(), token2.hashCode());
    }
}
