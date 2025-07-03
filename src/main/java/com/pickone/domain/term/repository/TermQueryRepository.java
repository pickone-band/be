package com.pickone.domain.term.repository;

import com.pickone.domain.term.model.entity.TermEntity;
import java.util.List;

public interface TermQueryRepository {
  List<TermEntity> findLatestTerms();
  List<Long> findRequiredLatestTermIds();
}
