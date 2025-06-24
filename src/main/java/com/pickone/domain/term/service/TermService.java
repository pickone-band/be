package com.pickone.domain.term.service;

import com.pickone.domain.term.model.entity.TermEntity;
import com.pickone.domain.term.repository.TermJpaRepository;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TermService {

  private final TermJpaRepository termJpaRepository;

  @Transactional
  public TermEntity create(TermEntity term) {
    log.info("약관 저장: title={}, required={}", term.getTitle(), term.isRequired());
    return termJpaRepository.save(term);
  }

  @Transactional(readOnly = true)
  public List<TermEntity> getRequiredTerms() {
    log.info("필수 약관 조회 실행");
    return termJpaRepository.findByRequiredTrue();
  }

  @Transactional(readOnly = true)
  public TermEntity getById(Long id) {
    log.info("약관 조회: id={}", id);
    return termJpaRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("약관 조회 실패 - 존재하지 않음: id={}", id);
          return new BusinessException(ErrorCode.TERM_NOT_FOUND);
        });
  }

  @Transactional(readOnly = true)
  public List<TermEntity> getAll() {
    log.info("전체 약관 조회 실행");
    return termJpaRepository.findAll();
  }

  @Transactional
  public void delete(Long id) {
    log.info("약관 삭제 실행: id={}", id);
    termJpaRepository.deleteById(id);
  }
}
