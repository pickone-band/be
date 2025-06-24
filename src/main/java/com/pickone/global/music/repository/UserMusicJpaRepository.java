package com.pickone.global.music.repository;

import com.pickone.domain.user.model.entity.UserMusicEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserMusicJpaRepository extends JpaRepository<UserMusicEntity, Long> {

}
