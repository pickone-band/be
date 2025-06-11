package com.PickOne.global.security.service;

import com.PickOne.domain.consent.model.entity.ConsentEntity;
import com.PickOne.domain.consent.repository.ConsentJpaRepository;
import com.PickOne.domain.term.model.entity.TermEntity;
import com.PickOne.domain.term.service.TermService;
import com.PickOne.domain.user.mapper.UserMapper;
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
  private final JwtService jwtService;
  private final RefreshTokenRepository refreshTokenRepository;
  private final TokenBlacklistRepository tokenBlacklistRepository;

  @Override
  @Transactional
  public AuthResult signup(SignupRequestDto request) {
    String email = request.email();
    String nickname = request.nickname();

    // 중복 검사
    if (userJpaRepository.findByEmail(email).isPresent()) {
      throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
    }
    if (userJpaRepository.findByNickname(nickname).isPresent()) {
      throw new BusinessException(ErrorCode.DUPLICATE_NICKNAME);
    }
    UserEntity user = new UserEntity(
            request.email(),
            Password.ofRaw(request.password(), passwordEncoder),
            request.nickname(),
            null,
            Role.USER,
            true,
            false,
            List.of(),
            List.of(),
            request.gender(),
            request.birthDate()
    );

    userJpaRepository.save(user);



    validateRequiredTerms(request.agreements());
    saveUserConsents(user, request.agreements());

    return issueTokens(UserMapper.toDomain(user));
  }

  @Override
  public AuthResult login(LoginRequest request) {
    UserEntity user = userJpaRepository.findByEmail(request.email())
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));

    if (!user.getPassword().matches(request.password(), passwordEncoder)) {
      throw new BusinessException(ErrorCode.INVALID_PASSWORD);
    }

    return issueTokens(UserMapper.toDomain(user));
  }

  @Override
  public AuthResult refresh(String refreshToken) {
    if (!jwtService.validateRefreshToken(refreshToken)) {
      throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
    }

    String email = jwtService.extractUsername(refreshToken);
    UserEntity user = userJpaRepository.findByEmail(email)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));

    return issueTokens(UserMapper.toDomain(user));
  }

  @Override
  public void logout(String accessToken) {
    jwtService.blacklistToken(accessToken);
  }

  private AuthResult issueTokens(User user) {
    String accessToken = jwtService.generateAccessToken(UserPrincipal.from(user));
    String refreshToken = jwtService.generateRefreshToken(UserPrincipal.from(user));
    refreshTokenRepository.save(user.getEmail().getValue(), refreshToken, jwtService.getRefreshTokenExpiration());

    return new AuthResult(accessToken, refreshToken, user.getEmail().getValue());
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
