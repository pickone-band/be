package com.pickone.domain.term.service;

import com.pickone.domain.term.dto.TermRequest;
import com.pickone.domain.term.dto.TermResponse;

public interface TermCommandService {
  TermResponse createTerm(TermRequest dto);
  void updateTerm(Long termId, TermRequest dto);
  void deleteTerm(Long termId);
}
