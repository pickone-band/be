package com.PickOne.global.auth.service;

import com.PickOne.domain.user.model.domain.Email;
import com.PickOne.domain.user.model.domain.Password;
import com.PickOne.domain.user.model.domain.User;
import com.PickOne.global.auth.model.domain.VerificationToken;
import com.PickOne.global.auth.repository.VerificationTokenRepository;
import com.PickOne.domain.user.repository.UserRepository;
import com.PickOne.global.auth.model.domain.EmailMessage;
import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(properties = {
        "mail.from=noreply@pickone.com",
        "mail.enabled=true"
})
@Transactional
class EmailVerificationServiceTest {

    @Autowired private EmailVerificationService emailVerificationService;
    @Autowired private VerificationTokenRepository tokenRepository;
    @Autowired private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setup() {
        Email email = Email.of("test@pickone.com");
        Password password = Password.of("password123");
        user = User.of(null, email, password, false);
        user = userRepository.save(user); // ← 여기서 ID 포함된 엔티티 반환받기
    }

    @Test
    void 인증_이메일_발송_성공() {
        emailVerificationService.sendVerificationEmail(user);

        Optional<VerificationToken> token = tokenRepository.findByUserIdAndTokenType(
                user.getId(), VerificationToken.TokenType.EMAIL_VERIFICATION);

        assertThat(token).isPresent();
        assertThat(token.get().getUserId()).isEqualTo(user.getId());
    }

    @Test
    void 이메일_인증_검증_성공() {
        emailVerificationService.sendVerificationEmail(user);
        String token = tokenRepository.findByUserIdAndTokenType(user.getId(),
                VerificationToken.TokenType.EMAIL_VERIFICATION).get().getToken();

        boolean result = emailVerificationService.verifyEmail(token);
        assertThat(result).isTrue();

        User verifiedUser = userRepository.findById(user.getId()).get();
        assertThat(verifiedUser.isVerified()).isTrue();
    }

    @Test
    void 만료된_토큰_검증_실패() {
        VerificationToken expired = new VerificationToken(
                "expired-token", user.getId(),
                VerificationToken.TokenType.EMAIL_VERIFICATION,
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1)
        );
        tokenRepository.save(expired);

        assertThatThrownBy(() -> emailVerificationService.verifyEmail("expired-token"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.EXPIRED_TOKEN.getMessage());
    }

}
