package com.pickone.domain.term.repository;

import com.pickone.domain.term.model.entity.TermEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TermJpaRepository extends JpaRepository<TermEntity, Long> {
  Optional<TermEntity> findByTitleAndVersion(String title, String version);
  boolean existsByTitleAndVersion(String title, String version);
  List<TermEntity> findByIsRequiredTrue();
}
