package com.pickone.domain.term.repository;

import com.pickone.domain.term.entity.Term;
import java.util.List;

public interface TermQueryRepository {
  List<Term> findLatestTerms();
  List<Long> findRequiredLatestTermIds();
}
