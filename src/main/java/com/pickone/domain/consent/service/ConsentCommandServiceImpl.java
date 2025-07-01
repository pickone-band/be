package com.pickone.domain.consent.service;

import com.pickone.domain.consent.dto.ConsentRequestDto;
import com.pickone.domain.consent.dto.ConsentResponseDto;
import com.pickone.domain.consent.model.entity.ConsentEntity;
import com.pickone.domain.consent.model.policy.ConsentPolicy;
import com.pickone.domain.consent.model.factory.ConsentFactory;
import com.pickone.domain.consent.model.mapper.ConsentMapper;
import com.pickone.domain.consent.repository.ConsentJpaRepository;
import com.pickone.domain.term.repository.TermJpaRepository;
import com.pickone.domain.user.repository.UserJpaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConsentCommandServiceImpl implements ConsentCommandService {
  private final UserJpaRepository userRepository;
  private final TermJpaRepository termRepository;
  private final ConsentJpaRepository consentRepository;
  private final ConsentPolicy consentPolicy;
  private final ConsentFactory consentFactory;

  @Transactional
  @Override
  public ConsentResponseDto saveConsent(Long userId, ConsentRequestDto dto) {
    var user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found"));
    var term = termRepository.findById(dto.termId())
        .orElseThrow(() -> new RuntimeException("Term not found"));

    consentPolicy.validateSaveConsent(user, term, dto.consented());

    ConsentEntity entity = consentFactory.create(user, term, dto.consented());
    ConsentEntity saved = consentRepository.save(entity);
    return ConsentMapper.toDto(saved);
  }

  @Transactional
  @Override
  public void deleteConsent(Long userId, Long termId) {
    var consent = consentRepository.findByUserIdAndTermId(userId, termId)
        .orElseThrow(() -> new RuntimeException("Consent not found"));
    consentRepository.delete(consent);
  }
}
