package com.PickOne.global.auth.model.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "token")
public class VerificationToken {
    private String token;
    private Long userId;
    private TokenType tokenType;
    private LocalDateTime createdAt;
    private LocalDateTime expiryDate;

    public VerificationToken(String token, Long userId, TokenType tokenType,
                             LocalDateTime createdAt, LocalDateTime expiryDate) {
        this.token = token;
        this.userId = userId;
        this.tokenType = tokenType;
        this.createdAt = createdAt;
        this.expiryDate = expiryDate;
    }

    public static VerificationToken createEmailVerificationToken(Long userId) {
        return new VerificationToken(
                UUID.randomUUID().toString(),
                userId,
                TokenType.EMAIL_VERIFICATION,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(24) // 24시간 유효
        );
    }

    public static VerificationToken createPasswordResetToken(Long userId) {
        return new VerificationToken(
                UUID.randomUUID().toString(),
                userId,
                TokenType.PASSWORD_RESET,
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1) // 1시간 유효
        );
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }

    public enum TokenType {
        EMAIL_VERIFICATION,
        PASSWORD_RESET
    }
}