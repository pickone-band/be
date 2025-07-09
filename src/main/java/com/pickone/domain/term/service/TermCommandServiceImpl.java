package com.pickone.domain.term.service;

import com.pickone.domain.term.dto.TermRequest;
import com.pickone.domain.term.dto.TermResponse;
import com.pickone.domain.term.dto.TermResponse;
import com.pickone.domain.term.entity.Term;
import com.pickone.domain.term.mapper.TermMapper;
import com.pickone.domain.term.repository.TermJpaRepository;
import com.pickone.domain.term.validator.TermValidator;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TermCommandServiceImpl implements TermCommandService {

  private final TermJpaRepository termRepository;
  private final TermMapper termMapper;
  private final TermValidator validator;

  @Transactional
  @Override
  public TermResponse createTerm(TermRequest dto) {
    log.info("[TermCommandService] 약관 생성 요청: title={}, version={}", dto.title(), dto.version());

    validator.validateCreate(dto);
    Term term = termMapper.toEntity(dto);
    Term saved = termRepository.save(term);

    log.info("[TermCommandService] 약관 저장 완료: id={}", saved.getId());
    return termMapper.toDto(saved);
  }

  @Transactional
  @Override
  public void updateTerm(Long termId, TermRequest dto) {
    log.info("[TermCommandService] 약관 수정 요청: id={}, title={}, version={}", termId, dto.title(), dto.version());

    Term term = termRepository.findById(termId)
        .orElseThrow(() -> {
          log.warn("[TermCommandService] 약관 수정 실패 - 존재하지 않음: id={}", termId);
          return new BusinessException(ErrorCode.TERM_NOT_FOUND);
        });

    validator.validateUpdate(term, dto);

    Term updated = term.withUpdate(dto.title(), dto.content(), dto.required(), dto.effectiveDate());
    termRepository.save(updated);

    log.info("[TermCommandService] 약관 수정 완료: id={}", updated.getId());
  }

  @Transactional
  @Override
  public void deleteTerm(Long termId) {
    log.info("[TermCommandService] 약관 삭제 요청: id={}", termId);

    if (!termRepository.existsById(termId)) {
      log.warn("[TermCommandService] 약관 삭제 실패 - 존재하지 않음: id={}", termId);
      throw new BusinessException(ErrorCode.TERM_NOT_FOUND);
    }

    termRepository.deleteById(termId);
    log.info("[TermCommandService] 약관 삭제 완료: id={}", termId);
  }
}
