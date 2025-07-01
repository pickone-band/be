package com.pickone.domain.term.service;

import com.pickone.domain.term.dto.TermRequestDto;
import com.pickone.domain.term.dto.TermResponseDto;
import com.pickone.domain.term.model.entity.TermEntity;
import com.pickone.domain.term.model.factory.TermFactory;
import com.pickone.domain.term.model.policy.TermPolicy;
import com.pickone.domain.term.model.mapper.TermMapper;
import com.pickone.domain.term.repository.TermJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TermCommandServiceImpl implements TermCommandService {
  private final TermJpaRepository termRepository;
  private final TermFactory termFactory;
  private final TermPolicy termPolicy;

  @Transactional
  @Override
  public TermResponseDto createTerm(TermRequestDto dto) {
    termPolicy.validateCreate(dto);
    TermEntity entity = termFactory.create(dto);
    TermEntity saved = termRepository.save(entity);
    return TermMapper.toDto(saved);
  }

  @Transactional
  @Override
  public void updateTerm(Long termId, TermRequestDto dto) {
    TermEntity term = termRepository.findById(termId)
        .orElseThrow(() -> new IllegalArgumentException("Term not found"));
    // 직접 업데이트 (여기선 불변 엔티티라면 새로 생성하여 교체해야 함)
    // or, setter 방식 사용
    throw new UnsupportedOperationException("불변 엔티티 업데이트는 별도 설계 필요");
  }

  @Transactional
  @Override
  public void deleteTerm(Long termId) {
    termRepository.deleteById(termId);
  }
}
