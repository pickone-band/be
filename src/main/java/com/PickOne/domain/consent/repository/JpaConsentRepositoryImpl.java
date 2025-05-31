package com.PickOne.domain.consent.repository;

import com.PickOne.domain.consent.mapper.ConsentMapper;
import com.PickOne.domain.consent.model.domain.Consent;
import com.PickOne.domain.consent.model.entity.ConsentEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JpaConsentRepositoryImpl implements ConsentRepository {

    private final ConsentJpaRepository consentJpaRepository;

    @Override
    public Consent save(Consent consent) {
        ConsentEntity saved = consentJpaRepository.save(ConsentMapper.toEntity(consent));
        return ConsentMapper.toDomain(saved);
    }

    @Override
    public Optional<Consent> findByUserIdAndTermsId(Long userId, Long termsId) {
        return consentJpaRepository.findByUserIdAndTermsId(userId, termsId)
                .map(ConsentMapper::toDomain);
    }

    @Override
    public List<Consent> findByUserId(Long userId) {
        return consentJpaRepository.findByUserId(userId).stream()
                .map(ConsentMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteByUserIdAndTermsId(Long userId, Long termsId) {
        consentJpaRepository.deleteByUserIdAndTermsId(userId, termsId);
    }
}
