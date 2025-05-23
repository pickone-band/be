package com.PickOne.global.auth.repository;

import com.PickOne.global.auth.model.domain.VerificationToken;
import com.PickOne.global.auth.model.domain.VerificationToken.TokenType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class VerificationTokenRepositoryImplTest {

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    private VerificationToken emailToken;
    private VerificationToken passwordToken;

    @BeforeEach
    void setUp() {
        emailToken = VerificationToken.createEmailVerificationToken(1001L);
        passwordToken = VerificationToken.createPasswordResetToken(1002L);
    }

    @Test
    void 이메일_토큰_저장_및_조회() {
        verificationTokenRepository.save(emailToken);

        Optional<VerificationToken> result = verificationTokenRepository.findByToken(emailToken.getToken());
        assertThat(result).isPresent();
        assertThat(result.get().getUserId()).isEqualTo(1001L);
        assertThat(result.get().getTokenType()).isEqualTo(TokenType.EMAIL_VERIFICATION);
        assertThat(result.get().isExpired()).isFalse();
    }

    @Test
    void 사용자와_토큰타입으로_조회() {
        verificationTokenRepository.save(passwordToken);

        Optional<VerificationToken> result = verificationTokenRepository.findByUserIdAndTokenType(1002L, TokenType.PASSWORD_RESET);
        assertThat(result).isPresent();
        assertThat(result.get().getToken()).isEqualTo(passwordToken.getToken());
    }

    @Test
    void 토큰_삭제() {
        verificationTokenRepository.save(emailToken);
        verificationTokenRepository.deleteByToken(emailToken.getToken());

        Optional<VerificationToken> result = verificationTokenRepository.findByToken(emailToken.getToken());
        assertThat(result).isNotPresent();
    }

    @Test
    void 만료된_토큰_삭제() {
        // 이미 만료된 토큰 생성
        VerificationToken expired = new VerificationToken(
                "expired-token-123",
                999L,
                TokenType.EMAIL_VERIFICATION,
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1)
        );
        verificationTokenRepository.save(expired);

        verificationTokenRepository.deleteExpiredTokens();

        Optional<VerificationToken> result = verificationTokenRepository.findByToken("expired-token-123");
        assertThat(result).isEmpty();
    }

    @Test
    void 토큰_만료_여부_확인() {
        VerificationToken expired = new VerificationToken(
                "expired-token-456",
                1003L,
                TokenType.PASSWORD_RESET,
                LocalDateTime.now().minusHours(2),
                LocalDateTime.now().minusHours(1)
        );
        assertThat(expired.isExpired()).isTrue();

        VerificationToken active = VerificationToken.createPasswordResetToken(1003L);
        assertThat(active.isExpired()).isFalse();
    }
}
