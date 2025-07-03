package com.pickone.domain.term.repository.impl;

import com.pickone.domain.term.model.entity.TermEntity;
import com.pickone.domain.term.repository.TermQueryRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TermQueryRepositoryImpl implements TermQueryRepository {
  private final EntityManager em;

  // 최신 버전 약관만 조회
  @Override
  public List<TermEntity> findLatestTerms() {
    // (예시) 버전 기준으로 최신 약관 목록 쿼리
    return em.createQuery(
        "SELECT t FROM TermEntity t WHERE t.effectiveDate = " +
            "(SELECT MAX(te.effectiveDate) FROM TermEntity te WHERE te.title = t.title)",
        TermEntity.class
    ).getResultList();
  }

  @Override
  public List<Long> findRequiredLatestTermIds() {
    return em.createQuery(
        "SELECT t.id FROM TermEntity t " +
            "WHERE t.effectiveDate = (" +
            "  SELECT MAX(te.effectiveDate) FROM TermEntity te " +
            "  WHERE te.title = t.title" +
            ") " +
            "AND t.isRequired = true", Long.class
    ).getResultList();
  }
}
