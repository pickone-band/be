package com.PickOne.domain.term.service;

import com.PickOne.domain.term.model.domain.Term;
import com.PickOne.term.model.domain.*;
import com.PickOne.domain.term.repository.TermRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class TermServiceImpl implements TermService {

    private final TermRepository termRepository;

    @Override
    @Transactional
    public Term createTerms(Term term) {
        return termRepository.save(term);
    }

    @Override
    @Transactional(readOnly = true)
    public Term getTermsById(Long id) {
        return termRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("약관을 찾을 수 없습니다: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Term> getRequiredTerms() {
        return termRepository.findRequired(LocalDateTime.now());
    }

    @Override
    public void deleteById(Long id) {
        termRepository.deleteById(id);
    }
}