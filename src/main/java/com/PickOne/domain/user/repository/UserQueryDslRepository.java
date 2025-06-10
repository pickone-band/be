package com.PickOne.domain.user.repository;

import com.PickOne.domain.user.model.domain.Genre;
import com.PickOne.domain.user.model.domain.Instrument;
import com.PickOne.domain.user.model.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserQueryDslRepository {
    Page<UserEntity> findAllByInstrument(Instrument instrument, Pageable pageable);
    Page<UserEntity> findAllByGenre(Genre genre, Pageable pageable);
    Page<UserEntity> search(String keyword, boolean onlyPublic, Pageable pageable);
}



