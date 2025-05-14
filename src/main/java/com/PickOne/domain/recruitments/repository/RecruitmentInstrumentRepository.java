package com.PickOne.domain.recruitments.repository;

import com.PickOne.domain.recruitments.model.entity.RecruitmentInstrument;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface RecruitmentInstrumentRepository extends JpaRepository<RecruitmentInstrument, Long> {
    List<RecruitmentInstrument> findAllByRecruitmentId(Long recruitmentId);
    @Modifying
    @Query("delete from RecruitmentInstrument ri where ri.recruitment.id = :recruitmentId")
    void deleteAllByRecruitmentId(Long recruitmentId);
}
