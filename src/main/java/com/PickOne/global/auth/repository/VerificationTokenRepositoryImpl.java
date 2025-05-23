package com.PickOne.global.auth.repository;

import com.PickOne.global.auth.model.domain.VerificationToken;
import com.PickOne.global.auth.model.entity.VerificationTokenEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class VerificationTokenRepositoryImpl implements VerificationTokenRepository {

    private final VerificationTokenJpaRepository jpaRepository;

    @Override
    public VerificationToken save(VerificationToken token) {
        VerificationTokenEntity entity = VerificationTokenEntity.fromDomain(token);
        return jpaRepository.save(entity).toDomain();
    }

    @Override
    public Optional<VerificationToken> findByToken(String token) {
        return jpaRepository.findByToken(token).map(VerificationTokenEntity::toDomain);
    }

    @Override
    public Optional<VerificationToken> findByUserIdAndTokenType(Long userId, VerificationToken.TokenType tokenType) {
        return jpaRepository.findByUserIdAndTokenType(userId, tokenType).map(VerificationTokenEntity::toDomain);
    }

    @Override
    public void deleteByToken(String token) {
        jpaRepository.findByToken(token).ifPresent(jpaRepository::delete);
    }

    @Override
    public void deleteExpiredTokens() {
        jpaRepository.deleteExpiredTokens(LocalDateTime.now());
    }
}