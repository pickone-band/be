package com.pickone.domain.term.repository;

import com.pickone.domain.term.model.entity.TermEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TermJpaRepository extends JpaRepository<TermEntity, Long> {

  List<TermEntity> findByRequiredTrue();
}