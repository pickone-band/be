package com.PickOne.domain.term.repository;

import com.PickOne.domain.term.model.domain.Term;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TermRepository {
    Term save(Term term);
    Optional<Term> findById(Long id);
    void deleteById(Long id);

    List<Term> findRequired(LocalDateTime currentDate);
}