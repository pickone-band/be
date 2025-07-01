package com.pickone.domain.term.service;

import com.pickone.domain.term.dto.TermResponseDto;

import java.util.List;

public interface TermQueryService {
  TermResponseDto getTerm(Long termId);
  List<TermResponseDto> getLatestTerms();
}
