package com.PickOne.domain.user.repository;

import com.PickOne.domain.user.model.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserQueryDslRepository {
    Page<UserEntity> findAllByInstrument(String instrumentName, Pageable pageable);

    Page<UserEntity> findAllByGenre(String genreName, Pageable pageable);

    Page<UserEntity> search(String keyword, boolean onlyPublic, Pageable pageable);
}



