package com.PickOne.global.verification.repository;

import com.PickOne.global.verification.model.domain.VerificationType;
import com.PickOne.global.verification.model.entity.VerificationTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface VerificationTokenJpaRepository extends JpaRepository<VerificationTokenEntity, Long> {

    Optional<VerificationTokenEntity> findByToken(String token);

    Optional<VerificationTokenEntity> findByUser_IdAndType(Long userId, VerificationType type);

    @Modifying
    @Query("DELETE FROM VerificationTokenEntity t WHERE t.expiredAt < :now")
    void deleteExpiredTokens(LocalDateTime now);
}