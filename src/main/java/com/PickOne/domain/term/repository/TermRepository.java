package com.PickOne.domain.term.repository;

import com.PickOne.domain.term.model.domain.Term;

import java.util.List;
import java.util.Optional;

public interface TermRepository {
    Term save(Term term);
    Optional<Term> findById(Long id);
    List<Term> findAll();
    void deleteById(Long id);
}
