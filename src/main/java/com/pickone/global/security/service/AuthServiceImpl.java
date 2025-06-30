package com.pickone.global.security.service;

import com.pickone.domain.consent.dto.ConsentTermtDto;
import com.pickone.domain.consent.model.entity.ConsentEntity;
import com.pickone.domain.consent.repository.ConsentJpaRepository;
import com.pickone.domain.term.model.entity.TermEntity;
import com.pickone.domain.term.service.TermService;
import com.pickone.domain.user.dto.SignupRequestDto;
import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.domain.user.service.UserJoinService;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import com.pickone.global.security.dto.*;
import com.pickone.global.security.model.entity.UserPrincipal;
import com.pickone.global.security.repository.RefreshTokenRepository;
import com.pickone.global.verification.service.EmailVerificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final UserJoinService userJoinService;
  private final UserJpaRepository userJpaRepository;
  private final EmailVerificationService emailVerificationService;
  private final PasswordEncoder passwordEncoder;
  private final ConsentJpaRepository consentJpaRepository;
  private final TermService termService;
  private final RefreshTokenRepository refreshTokenRepository;
  private final TokenProvider tokenProvider;

  @Override
  @Transactional
  public AuthResult signup(SignupRequestDto request) {
    log.info("회원가입 시작: email={}, nickname={}", request.email(), request.nickname());

    if (userJpaRepository.findByProfile_Email(request.email()).isPresent()) {
      throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
    }
    if (userJpaRepository.findByProfile_Nickname(request.nickname()).isPresent()) {
      throw new BusinessException(ErrorCode.DUPLICATE_NICKNAME);
    }

    validateRequiredTerms(request.agreements());

    userJoinService.join(request);  // join 메서드에서 UserEntity 저장까지 처리

    UserEntity user = userJpaRepository.findByProfile_Email(request.email())
        .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));

    emailVerificationService.sendVerificationEmail(user);

    return issueTokens(user); // 반드시 issueTokens 사용!
  }

  @Override
  public AuthResult login(LoginRequest request) {
    log.info("로그인 처리: email={}", request.email());

    UserEntity user = userJpaRepository.findByProfile_Email(request.email())
        .orElseThrow(() -> {
          log.warn("존재하지 않는 사용자 로그인 시도: {}", request.email());
          return new BusinessException(ErrorCode.USER_INFO_NOT_FOUND);
        });

    if (!passwordEncoder.matches(request.password(), user.getProfile().getPassword())) {
      log.warn("비밀번호 불일치: email={}", request.email());
      throw new BusinessException(ErrorCode.INVALID_PASSWORD);
    }

    if (!user.getStatus().isVerified()) {
      log.warn("이메일 미인증 사용자 로그인 시도: {}", request.email());
      throw new BusinessException(ErrorCode.EMAIL_NOT_VERIFIED);
    }

    return issueTokens(user);
  }

  @Override
  public AuthResult refresh(String refreshToken) {
    log.info("리프레시 토큰 검증 시작");
    if (!tokenProvider.validateRefreshToken(refreshToken)) {
      log.warn("유효하지 않은 리프레시 토큰");
      throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
    }

    String email = tokenProvider.extractUsername(refreshToken);
    UserEntity user = userJpaRepository.findByProfile_Email(email)
        .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));

    log.info("리프레시 토큰으로 사용자 인증 성공: email={}", email);
    return issueTokens(user);
  }

  @Override
  public void logout(String accessToken) {
    log.info("토큰 블랙리스트 추가: {}", accessToken);
    tokenProvider.blacklistToken(accessToken);
  }

  private AuthResult issueTokens(UserEntity userEntity) {
    UserPrincipal principal = UserPrincipal.from(userEntity);
    String accessToken = tokenProvider.generateAccessToken(principal);
    String refreshToken = tokenProvider.generateRefreshToken(principal);

    refreshTokenRepository.save(userEntity.getProfile().getEmail(), refreshToken, tokenProvider.getRefreshTokenExpiration());

    return new AuthResult(accessToken, refreshToken, userEntity.getProfile().getEmail());
  }

  private void validateRequiredTerms(List<ConsentTermtDto> agreements) {
    List<TermEntity> requiredTerms = termService.getAll().stream()
        .filter(TermEntity::isRequired)
        .toList();

    for (TermEntity term : requiredTerms) {
      boolean agreed = agreements.stream()
          .anyMatch(dto -> dto.termId().equals(term.getId()) && Boolean.TRUE.equals(dto.consented()));
      if (!agreed) {
        throw new BusinessException(ErrorCode.REQUIRED_TERM_NOT_AGREED);
      }
    }
  }

  // Additional consent saving method if needed
  private void saveUserConsents(UserEntity user, List<ConsentAgreementDto> agreements) {
    for (ConsentAgreementDto dto : agreements) {
      TermEntity term = termService.getById(dto.termId());
      ConsentEntity consent = new ConsentEntity(
          null,
          user,
          term,
          dto.consented(),
          LocalDateTime.now()
      );
      consentJpaRepository.save(consent);
    }
  }

  @Override
  @Transactional
  public void changePassword(String accessToken, ChangePasswordRequest request) {
    log.info("비밀번호 변경 처리 시작");

    String email = tokenProvider.extractUsername(accessToken);
    UserEntity user = userJpaRepository.findByProfile_Email(email)
        .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));

    if (!passwordEncoder.matches(request.currentPassword(), user.getProfile().getPassword())) {
      log.warn("현재 비밀번호 불일치: email={}", email);
      throw new BusinessException(ErrorCode.INVALID_PASSWORD);
    }
    if (passwordEncoder.matches(request.newPassword(), user.getProfile().getPassword())) {
      log.warn("기존과 동일한 비밀번호 사용 시도: email={}", email);
      throw new BusinessException(ErrorCode.SAME_AS_OLD_PASSWORD);
    }

    user.updatePassword(passwordEncoder.encode(request.newPassword()));
    userJpaRepository.save(user);
    log.info("비밀번호 변경 성공: email={}", email);
  }
}
