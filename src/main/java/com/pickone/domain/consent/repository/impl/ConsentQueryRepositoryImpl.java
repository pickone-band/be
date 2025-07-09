package com.pickone.domain.consent.repository.impl;

import com.pickone.domain.consent.repository.ConsentQueryRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ConsentQueryRepositoryImpl implements ConsentQueryRepository {

  private final EntityManager em;

  @Override
  public List<Long> findConsentedTermIdsByUserId(Long userId) {
    return em.createQuery(
            "SELECT c.term.id FROM Consent c " +
                "WHERE c.user.id = :userId AND c.consented = true", Long.class)
        .setParameter("userId", userId)
        .getResultList();
  }
}
