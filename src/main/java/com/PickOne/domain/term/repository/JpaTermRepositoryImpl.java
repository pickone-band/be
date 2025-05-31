package com.PickOne.domain.term.repository;

import com.PickOne.domain.term.mapper.TermMapper;
import com.PickOne.domain.term.model.domain.Term;
import com.PickOne.domain.term.model.entity.TermEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JpaTermRepositoryImpl implements TermRepository {

    private final TermJpaRepository termJpaRepository;

    @Override
    public Term save(Term term) {
        TermEntity saved = termJpaRepository.save(TermMapper.toEntity(term));
        return TermMapper.toDomain(saved);
    }

    @Override
    public Optional<Term> findById(Long id) {
        return termJpaRepository.findById(id)
                .map(TermMapper::toDomain);
    }

    @Override
    public List<Term> findAll() {
        return termJpaRepository.findAll().stream()
                .map(TermMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        termJpaRepository.deleteById(id);
    }
}
