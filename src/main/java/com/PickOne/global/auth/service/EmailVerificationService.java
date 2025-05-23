package com.PickOne.global.auth.service;

import com.PickOne.domain.user.model.domain.User;
import com.PickOne.domain.user.repository.UserRepository;
import com.PickOne.global.auth.model.domain.EmailMessage;
import com.PickOne.global.auth.model.domain.VerificationToken;
import com.PickOne.global.auth.repository.VerificationTokenRepository;
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
    private final UserRepository userRepository;

    @Value("${app.email.verification-required:true}")
    private boolean verificationRequired;

    /**
     * 이메일 인증 토큰 생성 및 발송
     */
    @Transactional
    public void sendVerificationEmail(User user) {
        // 기존 토큰이 있다면 삭제
        Optional<VerificationToken> existingToken = tokenRepository.findByUserIdAndTokenType(
                user.getId(), VerificationToken.TokenType.EMAIL_VERIFICATION);
        existingToken.ifPresent(token -> tokenRepository.deleteByToken(token.getToken()));

        // 새 토큰 생성
        VerificationToken token = VerificationToken.createEmailVerificationToken(user.getId());
        tokenRepository.save(token);

        // 이메일 생성 및 발송
        EmailMessage emailMessage = emailTemplateService.createVerificationEmail(
                user.getEmailValue(), token.getToken());
        emailSenderService.sendEmail(emailMessage);

        log.info("사용자 {}에게 인증 이메일을 발송했습니다", user.getId());
    }

    /**
     * 비밀번호 재설정 이메일 발송
     */
    @Transactional
    public void sendPasswordResetEmail(User user) {
        // 기존 토큰이 있다면 삭제
        Optional<VerificationToken> existingToken = tokenRepository.findByUserIdAndTokenType(
                user.getId(), VerificationToken.TokenType.PASSWORD_RESET);
        existingToken.ifPresent(token -> tokenRepository.deleteByToken(token.getToken()));

        // 새 토큰 생성
        VerificationToken token = VerificationToken.createPasswordResetToken(user.getId());
        tokenRepository.save(token);

        // 이메일 생성 및 발송
        EmailMessage emailMessage = emailTemplateService.createPasswordResetEmail(
                user.getEmailValue(), token.getToken());
        emailSenderService.sendEmail(emailMessage);

        log.info("사용자 {}에게 비밀번호 재설정 이메일을 발송했습니다", user.getId());
    }

    /**
     * 이메일 인증 토큰 검증
     */
    @Transactional
    public boolean verifyEmail(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_TOKEN));

        if (verificationToken.getTokenType() != VerificationToken.TokenType.EMAIL_VERIFICATION) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        if (verificationToken.isExpired()) {
            tokenRepository.deleteByToken(token);
            throw new BusinessException(ErrorCode.EXPIRED_TOKEN);
        }

        // 사용자 활성화
        User user = userRepository.findById(verificationToken.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));

        user.setVerified(true);
        userRepository.save(user);

        // 토큰 삭제
        tokenRepository.deleteByToken(token);

        // 환영 이메일 발송
        EmailMessage welcomeEmail = emailTemplateService.createWelcomeEmail(user.getEmailValue());
        emailSenderService.sendEmail(welcomeEmail);

        log.info("사용자 {}의 이메일 인증이 완료되었습니다", user.getId());
        return true;
    }

    /**
     * 비밀번호 재설정 토큰 검증
     */
    @Transactional
    public User validatePasswordResetToken(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_TOKEN));

        if (verificationToken.getTokenType() != VerificationToken.TokenType.PASSWORD_RESET) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        if (verificationToken.isExpired()) {
            tokenRepository.deleteByToken(token);
            throw new BusinessException(ErrorCode.EXPIRED_TOKEN);
        }

        // 사용자 조회
        return userRepository.findById(verificationToken.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));
    }

    /**
     * 사용자의 인증 상태 확인
     */
    public boolean isEmailVerificationRequired() {
        return verificationRequired;
    }

    /**
     * 사용자 인증 완료 후 토큰 삭제
     */
    @Transactional
    public void completePasswordReset(String token) {
        tokenRepository.deleteByToken(token);
    }

    /**
     * 만료된 토큰 정리 (매일 자정에 실행)
     */
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void cleanupExpiredTokens() {
        tokenRepository.deleteExpiredTokens();
        log.info("{}에 만료된 인증 토큰들이 정리되었습니다", LocalDateTime.now());
    }
}