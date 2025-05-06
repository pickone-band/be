package com.PickOne.domain.term.repository;

import com.PickOne.domain.term.model.entity.TermsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface JpaTermRepository extends JpaRepository<TermsEntity, Long> {
    @Query("SELECT t FROM TermsEntity t WHERE t.required = true AND t.effectiveDate <= :date ORDER BY t.type")
    List<TermsEntity> findRequired(@Param("date") LocalDateTime date);
}