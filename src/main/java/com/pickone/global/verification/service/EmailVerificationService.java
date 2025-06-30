package com.pickone.global.verification.service;

import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import com.pickone.global.verification.dto.EmailMessage;
import com.pickone.global.verification.model.domain.VerificationType;
import com.pickone.global.verification.model.entity.VerificationTokenEntity;
import com.pickone.global.verification.repository.VerificationTokenJpaRepository;
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
    log.info("이메일 인증 요청 발송: userId={}", user.getId());

    tokenRepository.findByUser_IdAndType(user.getId(), VerificationType.REGISTER)
        .ifPresent(token -> {
          log.debug("기존 인증 토큰 제거: {}", token.getToken());
          tokenRepository.delete(token);
        });

    VerificationTokenEntity token = VerificationTokenEntity.builder()
        .user(user)
        .email(user.getProfile().getEmail())
        .token(UUID.randomUUID().toString())
        .type(VerificationType.REGISTER)
        .expiredAt(LocalDateTime.now().plusHours(24))
        .isUsed(false)
        .build();

    tokenRepository.save(token);

    EmailMessage emailMessage = new EmailMessage(
        user.getProfile().getEmail(),
        "PickOne 회원가입 이메일 인증",
        emailTemplateService.createVerificationEmail(user.getProfile().getEmail(), token.getToken()).body(),
        true
    );

    emailSenderService.sendEmail(emailMessage);
    log.info("인증 이메일 발송 완료: userId={}, email={}", user.getId(), user.getProfile().getEmail());
  }

  @Transactional
  public void sendPasswordResetEmail(UserEntity user) {
    log.info("비밀번호 재설정 이메일 요청: userId={}", user.getId());

    tokenRepository.findByUser_IdAndType(user.getId(), VerificationType.RESET_PASSWORD)
        .ifPresent(token -> {
          log.debug("기존 비밀번호 재설정 토큰 제거: {}", token.getToken());
          tokenRepository.delete(token);
        });

    VerificationTokenEntity token = VerificationTokenEntity.builder()
        .user(user)
        .email(user.getProfile().getEmail())
        .token(UUID.randomUUID().toString())
        .type(VerificationType.RESET_PASSWORD)
        .expiredAt(LocalDateTime.now().plusHours(1))
        .isUsed(false)
        .build();

    tokenRepository.save(token);

    EmailMessage emailMessage = new EmailMessage(
        user.getProfile().getEmail(),
        "PickOne 비밀번호 재설정",
        emailTemplateService.createPasswordResetEmail(user.getProfile().getEmail(), token.getToken()).body(),
        true
    );

    emailSenderService.sendEmail(emailMessage);
    log.info("비밀번호 재설정 이메일 발송 완료: userId={}, email={}", user.getId(), user.getProfile().getEmail());
  }

  @Transactional
  public boolean verifyEmail(String token) {
    VerificationTokenEntity verificationToken = tokenRepository.findByToken(token)
        .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_TOKEN));

    if (verificationToken.getType() != VerificationType.REGISTER) {
      throw new BusinessException(ErrorCode.INVALID_TOKEN);
    }
    if (verificationToken.isExpired()) {
      tokenRepository.delete(verificationToken);
      throw new BusinessException(ErrorCode.EXPIRED_TOKEN);
    }

    UserEntity user = verificationToken.getUser();
    user.verifyEmail();
    userJpaRepository.save(user);
    tokenRepository.delete(verificationToken);

    // 이메일 발송 등 부가작업
    return true;
  }


  @Transactional
  public UserEntity validatePasswordResetToken(String token) {
    log.info("비밀번호 재설정 토큰 검증 요청");

    VerificationTokenEntity verificationToken = tokenRepository.findByToken(token)
        .orElseThrow(() -> {
          log.warn("유효하지 않은 비밀번호 재설정 토큰");
          return new BusinessException(ErrorCode.INVALID_TOKEN);
        });

    if (verificationToken.getType() != VerificationType.RESET_PASSWORD) {
      log.warn("비밀번호 재설정 토큰 타입 불일치");
      throw new BusinessException(ErrorCode.INVALID_TOKEN);
    }

    if (verificationToken.isExpired()) {
      log.warn("비밀번호 재설정 토큰 만료: {}", token);
      tokenRepository.delete(verificationToken);
      throw new BusinessException(ErrorCode.EXPIRED_TOKEN);
    }

    return verificationToken.getUser();
  }

  @Transactional
  public void completePasswordReset(String token) {
    log.debug("비밀번호 재설정 완료 처리: 토큰 제거 {}", token);
    tokenRepository.findByToken(token)
        .ifPresent(tokenRepository::delete);
  }

  @Scheduled(cron = "0 0 0 * * ?")
  @Transactional
  public void cleanupExpiredTokens() {
    tokenRepository.deleteExpiredTokens(LocalDateTime.now());
    log.info("만료된 이메일 인증 토큰 정리 완료");
  }
}
