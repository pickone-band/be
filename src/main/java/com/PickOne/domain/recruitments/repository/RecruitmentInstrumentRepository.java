package com.PickOne.domain.recruitments.repository;

import com.PickOne.domain.recruitments.model.entity.RecruitmentInstrument;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecruitmentInstrumentRepository extends JpaRepository<RecruitmentInstrument, Long> {
    List<RecruitmentInstrument> findAllByRecruitmentId(Long recruitmentId);
}
