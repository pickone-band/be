package com.pickone.domain.term.repository;

import com.pickone.domain.term.model.entity.TermEntity;
import java.time.LocalDateTime;
import java.util.List;

public interface TermQueryRepository {
  List<TermEntity> findRequiredTermsEffectiveAfter(LocalDateTime threshold);
  List<TermEntity> findByTitleKeyword(String keyword);
}