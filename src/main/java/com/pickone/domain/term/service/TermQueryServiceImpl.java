package com.pickone.domain.term.service;

import com.pickone.domain.term.dto.TermResponse;
import com.pickone.domain.term.mapper.TermMapper;
import com.pickone.domain.term.repository.TermJpaRepository;
import com.pickone.domain.term.repository.TermQueryRepository;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TermQueryServiceImpl implements TermQueryService {

  private final TermJpaRepository termRepository;
  private final TermQueryRepository termQueryRepository;
  private final TermMapper termMapper;

  @Override
  public TermResponse getTerm(Long termId) {
    log.info("[TermQueryService] 약관 단건 조회 요청: id={}", termId);

    return termRepository.findById(termId)
        .map(termMapper::toDto)
        .orElseThrow(() -> {
          log.warn("[TermQueryService] 약관 조회 실패 - 존재하지 않음: id={}", termId);
          return new BusinessException(ErrorCode.TERM_NOT_FOUND);
        });
  }

  @Override
  public List<TermResponse> getLatestTerms() {
    log.info("[TermQueryService] 최신 약관 전체 조회 요청");

    return termQueryRepository.findLatestTerms()
        .stream()
        .map(termMapper::toDto)
        .toList();
  }

  @Override
  public List<Long> getRequiredLatestTermIds() {
    log.info("[TermQueryService] 필수 최신 약관 ID 목록 조회 요청");

    return termQueryRepository.findRequiredLatestTermIds();
  }
}
