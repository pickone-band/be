package com.PickOne.domain.recruitments.repository;

import com.PickOne.domain.recruitments.model.entity.RecruitmentGenre;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecruitmentGenreRepository extends JpaRepository<RecruitmentGenre, Long> {
    List<RecruitmentGenre> findAllByRecruitmentId(Long recruitmentId);
}
