package com.PickOne.global.verification.service;

import com.PickOne.domain.user.model.entity.UserEntity;
import com.PickOne.domain.user.repository.UserJpaRepository;
import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
import com.PickOne.global.verification.dto.EmailMessage;
import com.PickOne.global.verification.model.domain.VerificationType;
import com.PickOne.global.verification.model.entity.VerificationTokenEntity;
import com.PickOne.global.verification.repository.VerificationTokenJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationService {

    private final EmailTemplateService emailTemplateService;
    private final EmailSenderService emailSenderService;
    private final VerificationTokenJpaRepository tokenRepository;
    private final UserJpaRepository userJpaRepository;

    @Value("${app.email.verification-required:true}")
    private boolean verificationRequired;

    @Transactional
    public void sendVerificationEmail(UserEntity user) {
        tokenRepository.findByUser_IdAndType(user.getId(), VerificationType.REGISTER)
                .ifPresent(tokenRepository::delete);

        VerificationTokenEntity token = VerificationTokenEntity.builder()
                .user(user)
                .email(user.getEmail())
                .token(UUID.randomUUID().toString())
                .type(VerificationType.REGISTER)
                .expiredAt(LocalDateTime.now().plusHours(24))
                .isUsed(false)
                .build();

        tokenRepository.save(token);

        EmailMessage emailMessage = new EmailMessage(
                user.getEmail(),
                "PickOne 회원가입 이메일 인증",
                emailTemplateService.createVerificationEmail(user.getEmail(), token.getToken()).body(),
                true
        );

        emailSenderService.sendEmail(emailMessage);
        log.info("사용자 {}에게 인증 이메일을 발송했습니다", user.getId());
    }

    @Transactional
    public void sendPasswordResetEmail(UserEntity user) {
        tokenRepository.findByUser_IdAndType(user.getId(), VerificationType.RESET_PASSWORD)
                .ifPresent(tokenRepository::delete);

        VerificationTokenEntity token = VerificationTokenEntity.builder()
                .user(user)
                .email(user.getEmail())
                .token(UUID.randomUUID().toString())
                .type(VerificationType.RESET_PASSWORD)
                .expiredAt(LocalDateTime.now().plusHours(1))
                .isUsed(false)
                .build();

        tokenRepository.save(token);

        EmailMessage emailMessage = new EmailMessage(
                user.getEmail(),
                "PickOne 비밀번호 재설정",
                emailTemplateService.createPasswordResetEmail(user.getEmail(), token.getToken()).body(),
                true
        );

        emailSenderService.sendEmail(emailMessage);
        log.info("사용자 {}에게 비밀번호 재설정 이메일을 발송했습니다", user.getId());
    }

    @Transactional
    public boolean verifyEmail(String token) {
        VerificationTokenEntity verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_TOKEN));

        if (verificationToken.getType() != VerificationType.REGISTER)
            throw new BusinessException(ErrorCode.INVALID_TOKEN);

        if (verificationToken.isExpired()) {
            tokenRepository.delete(verificationToken);
            throw new BusinessException(ErrorCode.EXPIRED_TOKEN);
        }

        UserEntity user = verificationToken.getUser();
        user.verify();
        userJpaRepository.save(user);
        tokenRepository.delete(verificationToken);

        EmailMessage welcomeEmail = new EmailMessage(
                user.getEmail(),
                "PickOne 가입을 환영합니다!",
                emailTemplateService.createWelcomeEmail(user.getEmail()).body(),
                true
        );

        emailSenderService.sendEmail(welcomeEmail);
        log.info("사용자 {}의 이메일 인증이 완료되었습니다", user.getId());
        return true;
    }

    @Transactional
    public UserEntity validatePasswordResetToken(String token) {
        VerificationTokenEntity verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_TOKEN));

        if (verificationToken.getType() != VerificationType.RESET_PASSWORD)
            throw new BusinessException(ErrorCode.INVALID_TOKEN);

        if (verificationToken.isExpired()) {
            tokenRepository.delete(verificationToken);
            throw new BusinessException(ErrorCode.EXPIRED_TOKEN);
        }

        return verificationToken.getUser();
    }

    @Transactional
    public void completePasswordReset(String token) {
        tokenRepository.findByToken(token)
                .ifPresent(tokenRepository::delete);
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanupExpiredTokens() {
        tokenRepository.deleteExpiredTokens(LocalDateTime.now());
        log.info("{}에 만료된 인증 토큰들을 정리했습니다.", LocalDateTime.now());
    }

    public boolean isEmailVerificationRequired() {
        return verificationRequired;
    }
}
