package com.PickOne.global.verification.service;

import com.PickOne.domain.user.model.entity.UserEntity;
import com.PickOne.global.common.enums.Genre;
import com.PickOne.global.common.enums.Mbti;
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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EmailVerificationServiceTest {

    @Mock
    private EmailTemplateService emailTemplateService;

    @Mock
    private EmailSenderService emailSenderService;

    @Mock
    private VerificationTokenRepository tokenRepository;

    @InjectMocks
    private EmailVerificationService emailVerificationService;

    private UserEntity user;

    @BeforeEach
    void setup() {
        user = UserEntity.builder()
                .email("test@example.com")
                .password("encoded-password")
                .nickname("테스트유저")
                .role(com.PickOne.domain.user.model.domain.Role.USER)
                .isPublic(true)
                .isOauth(false)
                .gender(com.PickOne.domain.user.model.domain.Gender.MALE)
                .birthDate(LocalDate.of(1990, 1, 1))
                .mbti(Mbti.ENTP)
                .genres(List.of(Genre.REGGAE, Genre.JAZZ))
                .build();
    }

    @Test
    void sendPasswordResetEmail_shouldSendEmailAndSaveToken() {
        // given
        given(tokenRepository.findByUserIdAndTokenType(anyLong(), any())).willReturn(Optional.empty());
        given(emailTemplateService.createPasswordResetEmail(anyString(), anyString()))
                .willReturn(EmailMessage.of("test@example.com", "subject", "body", true));

        // when
        emailVerificationService.sendPasswordResetEmail(user);

        // then
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
