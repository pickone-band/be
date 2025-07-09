package com.pickone.domain.application.repository;

import com.pickone.domain.application.model.entity.Application;
import com.pickone.domain.recruitments.model.entity.Recruitment;
import com.pickone.domain.user.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    boolean existsByUserAndRecruitment(User user, Recruitment recruitment);
    List<Application> findByRecruitment(Recruitment recruitment);
    Optional<Application> findByUserAndRecruitment(User user, Recruitment recruitment);
}
