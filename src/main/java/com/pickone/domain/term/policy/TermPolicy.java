package com.pickone.domain.term.policy;

import com.pickone.domain.term.model.entity.TermEntity;
import com.pickone.domain.term.repository.TermJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TermPolicy {

  private final TermJpaRepository termRepository;

  public List<TermEntity> getRequiredTerms() {
    return termRepository.findAll().stream()
        .filter(TermEntity::isRequired)
        .collect(Collectors.toList());
  }

  public List<TermEntity> getOptionalTerms() {
    return termRepository.findAll().stream()
        .filter(term -> !term.isRequired())
        .collect(Collectors.toList());
  }

  public List<TermEntity> getAllTerms() {
    return termRepository.findAll();
  }
}
