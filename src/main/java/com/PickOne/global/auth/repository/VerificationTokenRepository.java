package com.PickOne.global.auth.repository;

import com.PickOne.global.auth.model.domain.VerificationToken;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationTokenRepository {

    VerificationToken save(VerificationToken token);

    Optional<VerificationToken> findByToken(String token);

    Optional<VerificationToken> findByUserIdAndTokenType(Long userId, VerificationToken.TokenType tokenType);

    void deleteByToken(String token);

    void deleteExpiredTokens();
}