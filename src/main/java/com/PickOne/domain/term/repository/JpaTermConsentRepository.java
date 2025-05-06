package com.PickOne.domain.term.repository;

import com.PickOne.domain.term.model.entity.TermsConsentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaTermConsentRepository extends JpaRepository<TermsConsentEntity, Long> {

    Optional<TermsConsentEntity> findByUserIdAndTermsId(Long userId, Long termsId);
    List<TermsConsentEntity> findAllByUserId(Long userId);

    @Query("SELECT CASE WHEN COUNT(tc) > 0 THEN TRUE ELSE FALSE END FROM TermsConsentEntity tc WHERE tc.userId = :userId AND tc.termsId = :termsId AND tc.consented = true")
    boolean hasUserConsented(@Param("userId") Long userId, @Param("termsId") Long termsId);
}