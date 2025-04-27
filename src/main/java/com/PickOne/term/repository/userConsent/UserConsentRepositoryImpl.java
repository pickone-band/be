package com.PickOne.term.repository.userConsent;

import com.PickOne.term.model.domain.UserConsent;
import com.PickOne.term.model.entity.UserConsentEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * UserConsent 리포지토리 인터페이스의 JPA 구현체
 */
@Repository
@RequiredArgsConstructor
public class UserConsentRepositoryImpl implements UserConsentRepository {

    private final JpaUserConsentRepository jpaUserConsentRepository;

    @Override
    public UserConsent save(UserConsent userConsent) {
        // 기존 엔티티 확인
        Optional<UserConsentEntity> existingEntity =
                jpaUserConsentRepository.findByUserIdAndTermsId(
                        userConsent.getUserId(),
                        userConsent.getTermsId()
                );

        if (existingEntity.isPresent()) {
            // 기존 엔티티가 있으면 업데이트
            UserConsentEntity entity = existingEntity.get();
            entity.updateConsent(userConsent.isConsented(), userConsent.getConsentDate());
            return jpaUserConsentRepository.save(entity).toDomain();
        } else {
            // 새 엔티티 생성
            UserConsentEntity newEntity = UserConsentEntity.from(userConsent);
            return jpaUserConsentRepository.save(newEntity).toDomain();
        }
    }

    @Override
    public Optional<UserConsent> findByUserIdAndTermsId(Long userId, Long termsId) {
        return jpaUserConsentRepository.findByUserIdAndTermsId(userId, termsId)
                .map(UserConsentEntity::toDomain);
    }

    @Override
    public List<UserConsent> findAllByUserId(Long userId) {
        return jpaUserConsentRepository.findAllByUserId(userId)
                .stream()
                .map(UserConsentEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserConsent> findAllByTermsId(Long termsId) {
        return jpaUserConsentRepository.findAllByTermsId(termsId)
                .stream()
                .map(UserConsentEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasUserConsented(Long userId, Long termsId) {
        return jpaUserConsentRepository.hasUserConsented(userId, termsId);
    }

    @Override
    public long countConsentedTermsByUserIdAndTermsIds(Long userId, List<Long> termsIds) {
        return jpaUserConsentRepository.countConsentedTermsByUserIdAndTermsIds(userId, termsIds);
    }

    @Override
    public List<Long> findUserIdsByTermsIdAndConsented(Long termsId) {
        return jpaUserConsentRepository.findUserIdsByTermsIdAndConsented(termsId);
    }

    @Override
    public void deleteAll() {
        jpaUserConsentRepository.deleteAll();
    }
}