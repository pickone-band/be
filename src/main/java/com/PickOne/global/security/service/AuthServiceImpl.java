package com.PickOne.global.security.service;

import com.PickOne.domain.consent.model.entity.ConsentEntity;
import com.PickOne.domain.consent.repository.ConsentJpaRepository;
import com.PickOne.domain.term.model.entity.TermEntity;
import com.PickOne.domain.term.service.TermService;
import com.PickOne.domain.user.model.domain.*;
import com.PickOne.domain.user.model.entity.UserEntity;
import com.PickOne.domain.user.repository.UserJpaRepository;

import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
import com.PickOne.global.security.dto.ConsentAgreementDto;
import com.PickOne.global.security.dto.LoginRequest;
import com.PickOne.global.security.dto.SignupRequestDto;
import com.PickOne.global.security.dto.AuthResult;

import com.PickOne.global.security.model.entity.UserPrincipal;
import com.PickOne.global.security.repository.RefreshTokenRepository;
import com.PickOne.global.security.repository.TokenBlacklistRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final TermService termService;
  private final ConsentJpaRepository consentJpaRepository;
  private final UserJpaRepository userJpaRepository;
  private final PasswordEncoder passwordEncoder;
  private final TokenProvider tokenProvider;
  private final RefreshTokenRepository refreshTokenRepository;
  private final TokenBlacklistRepository tokenBlacklistRepository;

  @Override
  @Transactional
  public AuthResult signup(SignupRequestDto request) {
    String email = request.email();
    String nickname = request.nickname();

    if (userJpaRepository.findByEmail(email).isPresent()) {
      throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
    }
    if (userJpaRepository.findByNickname(nickname).isPresent()) {
      throw new BusinessException(ErrorCode.DUPLICATE_NICKNAME);
    }

    UserEntity user = UserEntity.builder()
            .email(request.email())
            .password(passwordEncoder.encode(request.password()))
            .nickname(request.nickname())
            .profileImage(null)
            .role(Role.USER)
            .isPublic(true)
            .isOauth(false)
            .gender(request.gender())
            .birthDate(request.birthDate())
            .mbti(null) // 회원가입에서 아직 받지 않음
            .genres(List.of()) // 기본 빈 장르
            .build();


// 비밀번호 비교도 직접 호출
    if (!passwordEncoder.matches(request.password(), user.getPassword())) {
      throw new BusinessException(ErrorCode.INVALID_PASSWORD);
    }

    userJpaRepository.save(user);

    validateRequiredTerms(request.agreements());
    saveUserConsents(user, request.agreements());

    return issueTokens(user); // <- 변경
  }

  @Override
  public AuthResult login(LoginRequest request) {
    UserEntity user = userJpaRepository.findByEmail(request.email())
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));

    if (!passwordEncoder.matches(request.password(), user.getPassword())) {
      throw new BusinessException(ErrorCode.INVALID_PASSWORD);
    }

    return issueTokens(user);
  }

  @Override
  public AuthResult refresh(String refreshToken) {
    if (!tokenProvider.validateRefreshToken(refreshToken)) {
      throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
    }

    String email = tokenProvider.extractUsername(refreshToken);
    UserEntity user = userJpaRepository.findByEmail(email)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));

    return issueTokens(user); // <- 변경
  }

  @Override
  public void logout(String accessToken) {
    tokenProvider.blacklistToken(accessToken);
  }

  private AuthResult issueTokens(UserEntity userEntity) { // <- User → UserEntity
    UserPrincipal principal = UserPrincipal.from(userEntity);
    String accessToken = tokenProvider.generateAccessToken(principal);
    String refreshToken = tokenProvider.generateRefreshToken(principal);

    refreshTokenRepository.save(userEntity.getEmail(), refreshToken, tokenProvider.getRefreshTokenExpiration());

    return new AuthResult(accessToken, refreshToken, userEntity.getEmail());
  }

  private void validateRequiredTerms(List<ConsentAgreementDto> agreements) {
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
}
