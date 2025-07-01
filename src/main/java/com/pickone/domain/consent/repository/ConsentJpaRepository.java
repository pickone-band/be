package com.pickone.domain.consent.repository;

import com.pickone.domain.consent.model.entity.ConsentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ConsentJpaRepository extends JpaRepository<ConsentEntity, Long> {
  Optional<ConsentEntity> findByUserIdAndTermId(Long userId, Long termId);
  List<ConsentEntity> findByUserId(Long userId);
  void deleteByUserIdAndTermId(Long userId, Long termId);
  boolean existsByUserIdAndTermIdAndConsentedTrue(Long userId, Long termId);
}
