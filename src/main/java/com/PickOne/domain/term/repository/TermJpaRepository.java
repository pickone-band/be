package com.PickOne.domain.term.repository;

import com.PickOne.domain.term.model.entity.TermEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TermJpaRepository extends JpaRepository<TermEntity, Long> {
}