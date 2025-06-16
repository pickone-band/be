// === EmailVerificationService - VO 제거 및 Entity 기반으로 변경 ===
package com.PickOne.global.verification.service;

import com.PickOne.domain.user.model.entity.UserEntity;
import com.PickOne.domain.user.repository.UserJpaRepository;
import com.PickOne.global.verification.model.domain.EmailMessage;
import com.PickOne.global.verification.model.domain.VerificationToken;
import com.PickOne.global.verification.repository.VerificationTokenRepository;
import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationService {

    private final EmailTemplateService emailTemplateService;
    private final EmailSenderService emailSenderService;
    private final VerificationTokenRepository tokenRepository;
    private final UserJpaRepository userJpaRepository;

    @Value("${app.email.verification-required:true}")
    private boolean verificationRequired;

    @Transactional
    public void sendVerificationEmail(UserEntity user) {
        Optional<VerificationToken> existingToken = tokenRepository.findByUserIdAndTokenType(
                user.getId(), VerificationToken.TokenType.EMAIL_VERIFICATION);
        existingToken.ifPresent(token -> tokenRepository.deleteByToken(token.getToken()));

        VerificationToken token = VerificationToken.createEmailVerificationToken(user.getId());
        tokenRepository.save(token);

        EmailMessage emailMessage = emailTemplateService.createVerificationEmail(
                user.getEmail(), token.getToken());
        emailSenderService.sendEmail(emailMessage);

        log.info("사용자 {}에게 인증 이메일을 발송했습니다", user.getId());
    }

    @Transactional
    public void sendPasswordResetEmail(UserEntity user) {
        Optional<VerificationToken> existingToken = tokenRepository.findByUserIdAndTokenType(
                user.getId(), VerificationToken.TokenType.PASSWORD_RESET);
        existingToken.ifPresent(token -> tokenRepository.deleteByToken(token.getToken()));

        VerificationToken token = VerificationToken.createPasswordResetToken(user.getId());
        tokenRepository.save(token);

        EmailMessage emailMessage = emailTemplateService.createPasswordResetEmail(
                user.getEmail(), token.getToken());
        emailSenderService.sendEmail(emailMessage);

        log.info("사용자 {}에게 비밀번호 재설정 이메일을 발송했습니다", user.getId());
    }

    @Transactional
    public boolean verifyEmail(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_TOKEN));

        if (verificationToken.getTokenType() != VerificationToken.TokenType.EMAIL_VERIFICATION)
            throw new BusinessException(ErrorCode.INVALID_TOKEN);

        if (verificationToken.isExpired()) {
            tokenRepository.deleteByToken(token);
            throw new BusinessException(ErrorCode.EXPIRED_TOKEN);
        }

        UserEntity user = userJpaRepository.findById(verificationToken.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));

        user.verify();
        userJpaRepository.save(user);
        tokenRepository.deleteByToken(token);

        EmailMessage welcomeEmail = emailTemplateService.createWelcomeEmail(user.getEmail());
        emailSenderService.sendEmail(welcomeEmail);

        log.info("사용자 {}의 이메일 인증이 완료되었습니다", user.getId());
        return true;
    }

    @Transactional
    public UserEntity validatePasswordResetToken(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_TOKEN));

        if (verificationToken.getTokenType() != VerificationToken.TokenType.PASSWORD_RESET)
            throw new BusinessException(ErrorCode.INVALID_TOKEN);

        if (verificationToken.isExpired()) {
            tokenRepository.deleteByToken(token);
            throw new BusinessException(ErrorCode.EXPIRED_TOKEN);
        }

        return userJpaRepository.findById(verificationToken.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));
    }

    public boolean isEmailVerificationRequired() {
        return verificationRequired;
    }

    @Transactional
    public void completePasswordReset(String token) {
        tokenRepository.deleteByToken(token);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void cleanupExpiredTokens() {
        tokenRepository.deleteExpiredTokens();
        log.info("{}에 만료된 인증 토큰들이 정리되었습니다", LocalDateTime.now());
    }
}