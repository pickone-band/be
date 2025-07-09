package com.pickone.domain.term.repository.impl;

import com.pickone.domain.term.entity.Term;
import com.pickone.domain.term.repository.TermQueryRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TermQueryRepositoryImpl implements TermQueryRepository {

  private final EntityManager em;

  @Override
  public List<Term> findLatestTerms() {
    return em.createQuery(
        "SELECT t FROM Term t WHERE t.effectiveDate = (" +
            "SELECT MAX(te.effectiveDate) FROM Term te WHERE te.title = t.title" +
            ")",
        Term.class
    ).getResultList();
  }

  @Override
  public List<Long> findRequiredLatestTermIds() {
    return em.createQuery(
        "SELECT t.id FROM Term t WHERE t.isRequired = true AND t.effectiveDate = (" +
            "SELECT MAX(te.effectiveDate) FROM Term te WHERE te.title = t.title" +
            ")",
        Long.class
    ).getResultList();
  }
}
