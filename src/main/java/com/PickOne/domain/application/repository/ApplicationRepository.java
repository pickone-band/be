package com.PickOne.domain.application.repository;

import com.PickOne.domain.application.model.entity.Application;
import com.PickOne.domain.recruitments.model.entity.Recruitment;
import com.PickOne.domain.user.model.entity.UserEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    boolean existsByUserEntityAndRecruitment(UserEntity user, Recruitment recruitment);
    List<Application> findByRecruitment(Recruitment recruitment);
}
