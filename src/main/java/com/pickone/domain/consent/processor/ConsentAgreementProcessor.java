package com.pickone.domain.consent.processor;

import com.pickone.domain.consent.dto.ConsentTermtDto;
import com.pickone.domain.consent.model.entity.ConsentEntity;
import com.pickone.domain.term.model.entity.TermEntity;
import com.pickone.domain.term.repository.TermJpaRepository;
import com.pickone.domain.term.policy.TermPolicy;
import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ConsentAgreementProcessor {

  private final TermJpaRepository termRepository;
  private final TermPolicy termPolicy;

  public List<ConsentEntity> toEntities(UserEntity user, List<ConsentTermtDto> agreementDtos) {
    // 필수 약관 ID 목록 확보
    Set<Long> requiredTermIds = termPolicy.getRequiredTerms().stream()
        .map(TermEntity::getId)
        .collect(Collectors.toSet());

    // 입력된 동의 맵핑 (termId -> dto)
    Map<Long, ConsentTermtDto> agreementMap = agreementDtos.stream()
        .collect(Collectors.toMap(ConsentTermtDto::termId, Function.identity()));

    // 모든 필수 약관에 대해 동의했는지 검증
    for (Long requiredId : requiredTermIds) {
      if (!agreementMap.containsKey(requiredId) || !agreementMap.get(requiredId).consented()) {
        throw new BusinessException(ErrorCode.REQUIRED_TERM_NOT_AGREED);
      }
    }

    // ConsentEntity 생성
    return agreementDtos.stream()
        .map(dto -> {
          TermEntity term = termRepository.findById(dto.termId())
              .orElseThrow(() -> new BusinessException(ErrorCode.TERM_NOT_FOUND));

          return new ConsentEntity(
              null, // id
              user,
              term,
              dto.consented(),
              LocalDateTime.now()
          );
        })
        .collect(Collectors.toList());
  }
}
