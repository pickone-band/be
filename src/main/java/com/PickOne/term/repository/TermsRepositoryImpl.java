package com.PickOne.term.repository;

import com.PickOne.term.model.domain.Terms;
import com.PickOne.term.model.domain.TermsType;
import com.PickOne.term.model.entity.TermsEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Terms 리포지토리 인터페이스의 JPA 구현체
 */
@Repository
@RequiredArgsConstructor
public class TermsRepositoryImpl implements TermsRepository {

    private final JpaTermsRepository jpaTermsRepository;

    @Override
    public Terms save(Terms terms) {
        TermsEntity entity = TermsEntity.from(terms);
        TermsEntity savedEntity = jpaTermsRepository.save(entity);
        return savedEntity.toDomain();
    }

    @Override
    public Optional<Terms> findById(Long id) {
        return jpaTermsRepository.findById(id)
                .map(TermsEntity::toDomain);
    }

    @Override
    public Optional<Terms> findLatestByType(TermsType type) {
        return jpaTermsRepository.findLatestByType(type)
                .map(TermsEntity::toDomain);
    }

    @Override
    public Optional<Terms> findCurrentlyEffectiveByType(TermsType type, LocalDate currentDate) {
        return jpaTermsRepository.findCurrentlyEffectiveByType(type, currentDate)
                .map(TermsEntity::toDomain);
    }

    @Override
    public List<Terms> findAllRequiredAndEffective(LocalDate currentDate) {
        return jpaTermsRepository.findAllRequiredAndEffective(currentDate)
                .stream()
                .map(TermsEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Terms> findAllByType(TermsType type) {
        return jpaTermsRepository.findAllByTypeOrderByVersionDesc(type)
                .stream()
                .map(TermsEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Terms> findAllUpcomingTerms(LocalDate date) {
        return jpaTermsRepository.findAllByEffectiveDateAfterOrderByEffectiveDateAsc(date)
                .stream()
                .map(TermsEntity::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsById(Long id) {
        return jpaTermsRepository.existsById(id);
    }

    @Override
    public void deleteById(Long id) {
        jpaTermsRepository.deleteById(id);
    }
}