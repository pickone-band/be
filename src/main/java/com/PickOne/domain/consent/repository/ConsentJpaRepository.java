package com.PickOne.domain.consent.repository;

import com.PickOne.domain.consent.model.entity.ConsentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConsentJpaRepository extends JpaRepository<ConsentEntity, Long> {
    Optional<ConsentEntity> findByUserIdAndTermsId(Long userId, Long termsId);
    List<ConsentEntity> findByUserId(Long userId);
    void deleteByUserIdAndTermsId(Long userId, Long termsId);
}
