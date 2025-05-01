package com.PickOne.domain.recruitments.repository;

import com.PickOne.domain.recruitments.model.entity.Recruitment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecruitmentRepository extends JpaRepository<Recruitment, Long>, RecruitmentRepositoryCustom {
}
