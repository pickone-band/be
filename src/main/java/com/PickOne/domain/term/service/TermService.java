package com.PickOne.domain.term.service;

import java.util.List;


import com.PickOne.domain.term.model.entity.TermEntity;
import com.PickOne.domain.term.repository.TermJpaRepository;
import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TermService {

    private final TermJpaRepository termjpaRepository;

    @Transactional
    public TermEntity create(TermEntity term) {
        return termjpaRepository.save(term);
    }

    @Transactional(readOnly = true)
    public TermEntity getById(Long id) {
        return termjpaRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.TERM_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<TermEntity> getAll() {
        return termjpaRepository.findAll();
    }

    @Transactional
    public void delete(Long id) {
        termjpaRepository.deleteById(id);
    }
}
