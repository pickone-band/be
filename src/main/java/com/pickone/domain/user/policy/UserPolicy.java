package com.pickone.domain.user.policy;

import com.pickone.domain.consent.dto.ConsentRequest;
import com.pickone.domain.term.service.TermQueryService;
import com.pickone.domain.user.dto.SignupRequest;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import com.pickone.global.oauth2.model.domain.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserPolicy {

  private final UserJpaRepository userRepository;
  private final TermQueryService termQueryService;

  public void validateSignup(SignupRequest dto) {
    if (dto.email() == null || dto.email().isBlank()) {
      throw new BusinessException(ErrorCode.INVALID_EMAIL);
    }

    if (userRepository.existsByProfileEmail(dto.email())) {
      throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
    }

    if (dto.password() == null || dto.password().isBlank()) {
      throw new BusinessException(ErrorCode.INVALID_PASSWORD);
    }

    if (dto.nickname() == null || dto.nickname().isBlank()) {
      throw new BusinessException(ErrorCode.INVALID_NICKNAME);
    }

    if (userRepository.existsByProfileNickname(dto.nickname())) {
      throw new BusinessException(ErrorCode.DUPLICATE_NICKNAME);
    }

    if (dto.birthDate() == null) {
      throw new BusinessException(ErrorCode.INVALID_BIRTH);
    }

    if (dto.gender() == null) {
      throw new BusinessException(ErrorCode.INVALID_GENDER);
    }

    if (dto.terms() == null || dto.terms().isEmpty()) {
      throw new BusinessException(ErrorCode.REQUIRED_TERM_NOT_AGREED);
    }

    List<Long> requiredTermIds = termQueryService.getRequiredLatestTermIds();

    Set<Long> consentedIds = dto.terms().stream()
        .filter(ConsentRequest::consented)
        .map(ConsentRequest::termId)
        .collect(Collectors.toSet());

    boolean allRequiredAgreed = requiredTermIds.stream()
        .allMatch(consentedIds::contains);

    if (!allRequiredAgreed) {
      throw new BusinessException(ErrorCode.REQUIRED_TERM_NOT_AGREED);
    }
  }

  public void validateSignupWithOAuth2(OAuth2UserInfo userInfo) {
    if (userInfo.getEmail() == null || userInfo.getEmail().isBlank()) {
      throw new BusinessException(ErrorCode.INVALID_EMAIL);
    }

    if (userInfo.getNickname() == null || userInfo.getNickname().isBlank()) {
      throw new BusinessException(ErrorCode.INVALID_NICKNAME);
    }
  }
}
