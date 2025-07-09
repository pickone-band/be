package com.pickone.domain.term.repository;

import com.pickone.domain.term.entity.Term;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TermJpaRepository extends JpaRepository<Term, Long> {
  Optional<Term> findByTitleAndVersion(String title, String version);
  boolean existsByTitleAndVersion(String title, String version);
  List<Term> findByIsRequiredTrue();
}