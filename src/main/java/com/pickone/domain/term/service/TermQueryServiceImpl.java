package com.pickone.domain.term.service;

import com.pickone.domain.term.dto.TermResponseDto;
import com.pickone.domain.term.model.mapper.TermMapper;
import com.pickone.domain.term.repository.TermJpaRepository;
import com.pickone.domain.term.repository.TermQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TermQueryServiceImpl implements TermQueryService {
  private final TermJpaRepository termRepository;
  private final TermQueryRepository termQueryRepository;

  @Override
  public TermResponseDto getTerm(Long termId) {
    return termRepository.findById(termId)
        .map(TermMapper::toDto)
        .orElseThrow(() -> new IllegalArgumentException("Term not found"));
  }

  @Override
  public List<TermResponseDto> getLatestTerms() {
    return termQueryRepository.findLatestTerms()
        .stream().map(TermMapper::toDto).toList();
  }
}
