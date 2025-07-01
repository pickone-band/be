package com.pickone.domain.term.service;

import com.pickone.domain.term.dto.TermRequestDto;
import com.pickone.domain.term.dto.TermResponseDto;

public interface TermCommandService {
  TermResponseDto createTerm(TermRequestDto dto);
  void updateTerm(Long termId, TermRequestDto dto);
  void deleteTerm(Long termId);
}
