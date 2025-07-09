package com.pickone.domain.term.service;

import com.pickone.domain.term.dto.TermResponse;

import java.util.List;

public interface TermQueryService {
  TermResponse getTerm(Long termId);
  List<TermResponse> getLatestTerms();
  List<Long> getRequiredLatestTermIds();
}
