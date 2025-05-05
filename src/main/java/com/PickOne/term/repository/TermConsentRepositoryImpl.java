package com.PickOne.term.repository;

import com.PickOne.term.model.domain.TermConsent;
import com.PickOne.term.model.entity.TermsConsentEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class TermConsentRepositoryImpl implements TermConsentRepository {
    private final JpaTermConsentRepository jpaRepository;

    @Override
    public TermConsent save(TermConsent termConsent) {
        Optional<TermsConsentEntity> existingEntity = jpaRepository.findByUserIdAndTermsId(
                termConsent.getUserId(), termConsent.getTermsId());

        TermsConsentEntity entity;
        if (existingEntity.isPresent()) {
            entity = existingEntity.get();
            entity.updateConsent(termConsent.isConsented(), termConsent.getConsentDate());
        } else {
            entity = TermsConsentEntity.from(termConsent);
        }

        return jpaRepository.save(entity).toDomain();
    }

    @Override
    public Optional<TermConsent> findByUserIdAndTermsId(Long userId, Long termsId) {
        return jpaRepository.findByUserIdAndTermsId(userId, termsId)
                .map(TermsConsentEntity::toDomain);
    }

    @Override
    public List<TermConsent> findAllByUserId(Long userId) {
        return jpaRepository.findAllByUserId(userId)
                .stream()
                .map(TermsConsentEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasUserConsented(Long userId, Long termsId) {
        return jpaRepository.hasUserConsented(userId, termsId);
    }
}