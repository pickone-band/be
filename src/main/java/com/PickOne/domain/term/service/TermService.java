package com.PickOne.domain.term.service;

import java.util.List;


import com.PickOne.domain.term.model.domain.Term;
import com.PickOne.domain.term.repository.TermRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TermService {

    private final TermRepository termRepository;

    @Transactional
    public Term create(Term term) {
        return termRepository.save(term);
    }

    @Transactional(readOnly = true)
    public Term getById(Long id) {
        return termRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("약관을 찾을 수 없습니다. ID=" + id));
    }

    @Transactional(readOnly = true)
    public List<Term> getAll() {
        return termRepository.findAll();
    }

    @Transactional
    public void delete(Long id) {
        termRepository.deleteById(id);
    }
}
