package com.pickone.domain.consent.repository;

import com.pickone.domain.consent.entity.Consent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ConsentJpaRepository extends JpaRepository<Consent, Long> {
  Optional<Consent> findByUserIdAndTermId(Long userId, Long termId);
  List<Consent> findByUserId(Long userId);
  void deleteByUserIdAndTermId(Long userId, Long termId);
  boolean existsByUserIdAndTermIdAndConsentedTrue(Long userId, Long termId);
}