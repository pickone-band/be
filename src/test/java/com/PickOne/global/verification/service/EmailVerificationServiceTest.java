package com.PickOne.global.verification.service;

import com.PickOne.domain.user.model.domain.*;
import com.PickOne.domain.user.repository.UserRepository;
import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.verification.model.domain.EmailMessage;
import com.PickOne.global.verification.model.domain.VerificationToken;
import com.PickOne.global.verification.repository.VerificationTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailVerificationServiceTest {

    @Mock
    private EmailTemplateService emailTemplateService;
    @Mock private EmailSenderService emailSenderService;
    @Mock private VerificationTokenRepository tokenRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private EmailVerificationService emailVerificationService;

    private User user;

    @BeforeEach
    void setup() {
        user = new User(1L, Email.of("test@example.com"), null,  new Nickname("닉네임"),
                new ProfileImage("https://img.example.com"),
                true,
                false,
                false,
                Role.USER,
                List.of(new Instrument("ELECTRIC_GUITAR")),
                List.of(new Genre("ROCK"))
        );
    }

    @Test
    void sendPasswordResetEmail_shouldSendEmailAndSaveToken() {
        given(tokenRepository.findByUserIdAndTokenType(anyLong(), any())).willReturn(Optional.empty());
        given(emailTemplateService.createPasswordResetEmail(anyString(), anyString()))
                .willReturn(EmailMessage.of("test@example.com", "subject", "body", true));

        emailVerificationService.sendPasswordResetEmail(user);

        verify(tokenRepository).save(any(VerificationToken.class));
        verify(emailSenderService).sendEmail(any(EmailMessage.class));
    }

    @Test
    void verifyEmail_shouldThrowIfTokenInvalid() {
        given(tokenRepository.findByToken(anyString())).willReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> emailVerificationService.verifyEmail("invalid-token"));
    }

    @Test
    void validatePasswordResetToken_shouldThrowIfExpired() {
        VerificationToken token = mock(VerificationToken.class);
        given(token.getTokenType()).willReturn(VerificationToken.TokenType.PASSWORD_RESET);
        given(token.isExpired()).willReturn(true);
        given(tokenRepository.findByToken(anyString())).willReturn(Optional.of(token));

        assertThrows(BusinessException.class, () -> emailVerificationService.validatePasswordResetToken("expired-token"));
    }

    @Test
    void completePasswordReset_shouldDeleteToken() {
        emailVerificationService.completePasswordReset("some-token");
        verify(tokenRepository).deleteByToken("some-token");
    }
}
