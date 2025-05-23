package com.PickOne.global.auth.repository;

import com.PickOne.global.auth.model.domain.VerificationToken;
import com.PickOne.global.auth.model.entity.VerificationTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface VerificationTokenJpaRepository extends JpaRepository<VerificationTokenEntity, Long> {

    VerificationToken save(VerificationToken token);

    Optional<VerificationTokenEntity> findByToken(String token);

    Optional<VerificationTokenEntity> findByUserIdAndTokenType(Long userId, VerificationToken.TokenType tokenType);

    @Modifying
    @Query("DELETE FROM VerificationTokenEntity t WHERE t.expiryDate < ?1")
    void deleteExpiredTokens(LocalDateTime now); 
}