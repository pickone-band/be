package com.PickOne.domain.term.repository;

import com.PickOne.domain.term.model.domain.Term;
import com.PickOne.domain.term.model.entity.TermsEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class TermRepositoryImpl implements TermRepository {

    private final JpaTermRepository jpaRepository;

    @Override
    public Term save(Term term) {
        TermsEntity entity = TermsEntity.from(term);
        TermsEntity savedEntity = jpaRepository.save(entity);
        return savedEntity.toDomain();
    }

    @Override
    public Optional<Term> findById(Long id) {
        return jpaRepository.findById(id)
                .map(TermsEntity::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public List<Term> findRequired(LocalDateTime currentDate) {
        return jpaRepository.findRequired(currentDate)
                .stream()
                .map(TermsEntity::toDomain)
                .collect(Collectors.toList());
    }
}