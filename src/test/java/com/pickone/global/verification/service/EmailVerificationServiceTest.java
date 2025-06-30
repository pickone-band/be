package com.pickone.global.verification.service;

import com.pickone.domain.user.model.domain.Gender;
import com.pickone.domain.user.model.domain.Role;
import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.global.common.enums.Genre;
import com.pickone.global.common.enums.Mbti;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.verification.dto.EmailMessage;
import com.pickone.global.verification.model.domain.VerificationType;
import com.pickone.global.verification.model.entity.VerificationTokenEntity;
import com.pickone.global.verification.repository.VerificationTokenJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailVerificationServiceTest {

    @Mock private EmailTemplateService emailTemplateService;
    @Mock private EmailSenderService emailSenderService;
    @Mock private VerificationTokenJpaRepository tokenRepository;
    @InjectMocks private EmailVerificationService emailVerificationService;

    private UserEntity user;

    @BeforeEach
    void setup() {
      user = UserEntity.of(
          "test@example.com",
          "encoded-password",
          "tester",
          Gender.MALE,
          LocalDate.of(1995, 1, 1),
          Mbti.INFP,
          List.of(Genre.JAZZ, Genre.REGGAE)
      );


    }

    @Test
    void sendPasswordResetEmail_shouldSendEmailAndSaveToken() {
        given(tokenRepository.findByUser_IdAndType(nullable(Long.class), eq(VerificationType.RESET_PASSWORD)))
                .willReturn(Optional.empty());
        given(emailTemplateService.createPasswordResetEmail(anyString(), anyString()))
                .willReturn(new EmailMessage("test@example.com", "subject", "body", true));

        emailVerificationService.sendPasswordResetEmail(user);

        verify(tokenRepository).save(any(VerificationTokenEntity.class));
        verify(emailSenderService).sendEmail(any(EmailMessage.class));
    }

    @Test
    void verifyEmail_shouldThrowIfTokenInvalid() {
        given(tokenRepository.findByToken(anyString())).willReturn(Optional.empty());
        assertThrows(BusinessException.class, () -> emailVerificationService.verifyEmail("invalid-token"));
    }

    @Test
    void validatePasswordResetToken_shouldThrowIfExpired() {
        VerificationTokenEntity token = VerificationTokenEntity.builder()
                .token(UUID.randomUUID().toString())
                .type(VerificationType.RESET_PASSWORD)
                .email(user.getProfile().getEmail())
                .expiredAt(LocalDateTime.now().minusMinutes(1))
                .user(user)
                .isUsed(false)
                .build();

        given(tokenRepository.findByToken(anyString())).willReturn(Optional.of(token));

        assertThrows(BusinessException.class, () -> emailVerificationService.validatePasswordResetToken("expired-token"));
    }

    @Test
    void completePasswordReset_shouldDeleteToken() {
        VerificationTokenEntity token = VerificationTokenEntity.builder()
                .token("token-to-delete")
                .user(user)
                .type(VerificationType.RESET_PASSWORD)
                .email(user.getProfile().getEmail())
                .expiredAt(LocalDateTime.now().plusHours(1))
                .isUsed(false)
                .build();

        given(tokenRepository.findByToken("token-to-delete")).willReturn(Optional.of(token));

        emailVerificationService.completePasswordReset("token-to-delete");

        verify(tokenRepository).delete(token);
    }
}
