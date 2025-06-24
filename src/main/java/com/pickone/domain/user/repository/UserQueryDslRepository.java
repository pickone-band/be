package com.pickone.domain.user.repository;

import com.pickone.domain.user.dto.UserSearchConditionDto;
import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.global.common.enums.Genre;
import com.pickone.global.common.enums.Instrument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserQueryDslRepository {

  Page<UserEntity> searchUsers(UserSearchConditionDto condition, Pageable pageable);

  default Page<UserEntity> searchByInstrument(Instrument instrument, Pageable pageable) {
    UserSearchConditionDto cond = UserSearchConditionDto.builder().instruments(List.of(instrument))
        .build();
    return searchUsers(cond, pageable);
  }

  default Page<UserEntity> searchByGenre(Genre genre, Pageable pageable) {
    UserSearchConditionDto cond = UserSearchConditionDto.builder().genres(List.of(genre)).build();
    return searchUsers(cond, pageable);
  }
}
