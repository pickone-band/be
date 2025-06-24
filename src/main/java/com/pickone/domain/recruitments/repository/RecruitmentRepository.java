package com.pickone.domain.recruitments.repository;

import com.pickone.domain.recruitments.model.entity.Recruitment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecruitmentRepository extends JpaRepository<Recruitment, Long>, RecruitmentRepositoryCustom {
}
