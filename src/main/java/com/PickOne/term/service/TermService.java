package com.PickOne.term.service;

import com.PickOne.term.model.domain.Term;

import java.util.List;

public interface TermService {
    Term createTerms(Term term);
    Term getTermsById(Long id);
    List<Term> getRequiredTerms();
    void deleteById(Long id);
}