package com.pickone.domain.recruitments.repository;

import com.pickone.domain.recruitments.model.entity.RecruitmentGenre;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface RecruitmentGenreRepository extends JpaRepository<RecruitmentGenre, Long> {
    List<RecruitmentGenre> findAllByRecruitmentId(Long recruitmentId);
    @Modifying
    @Query("delete from RecruitmentGenre rg where rg.recruitment.id = :recruitmentId")
    void deleteAllByRecruitmentId(Long recruitmentId);
}
