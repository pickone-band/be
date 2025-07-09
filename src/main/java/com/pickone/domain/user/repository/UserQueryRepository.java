package com.pickone.domain.user.repository;

import com.pickone.domain.user.dto.UserSearchCondition;
import com.pickone.domain.user.entity.User;
import com.pickone.global.common.enums.Genre;
import com.pickone.global.common.enums.Instrument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserQueryRepository {

  Page<User> searchUsers(UserSearchCondition condition, Pageable pageable);

  default Page<User> searchByInstrument(Instrument instrument, Pageable pageable) {
    return searchUsers(UserSearchCondition.builder()
        .instruments(List.of(instrument))
        .build(), pageable);
  }

  default Page<User> searchByGenre(Genre genre, Pageable pageable) {
    return searchUsers(UserSearchCondition.builder()
        .genres(List.of(genre))
        .build(), pageable);
  }

  Page<User> searchByKeywordAndPublic(String keyword, Boolean onlyPublic, Pageable pageable);
}
