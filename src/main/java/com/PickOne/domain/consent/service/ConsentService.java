package com.PickOne.domain.consent.service;

import com.PickOne.domain.consent.dto.ConsentRequestDto;
import com.PickOne.domain.consent.model.entity.ConsentEntity;
import com.PickOne.domain.consent.repository.ConsentJpaRepository;
import com.PickOne.domain.term.model.entity.TermEntity;
import com.PickOne.domain.term.repository.TermJpaRepository;
import com.PickOne.domain.user.model.entity.UserEntity;
import com.PickOne.domain.user.repository.UserJpaRepository;
import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsentService {

    private final ConsentJpaRepository consentRepository;
    private final UserJpaRepository userRepository;
    private final TermJpaRepository termRepository;

    @Transactional
    public ConsentEntity saveConsent(Long userId, Long termId, ConsentRequestDto requestDto) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));

        TermEntity term = termRepository.findById(termId)
                .orElseThrow(() -> new BusinessException(ErrorCode.TERM_NOT_FOUND));

        ConsentEntity entity = requestDto.toEntity(user, term);
        return consentRepository.save(entity);
    }

    @Transactional(readOnly = true)
    public List<ConsentEntity> getUserConsents(Long userId) {
        return consentRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public boolean hasConsented(Long userId, Long termId) {
        return consentRepository.findByUserIdAndTermId(userId, termId)
                .map(ConsentEntity::isConsented)
                .orElse(false);
    }

    @Transactional
    public void deleteConsent(Long userId, Long termId) {
        consentRepository.deleteByUserIdAndTermId(userId, termId);
    }
}
