package com.pickone.domain.user.repository.impl;

import com.pickone.domain.user.dto.UserSearchCondition;
import com.pickone.domain.user.entity.User;
import com.pickone.domain.user.repository.UserQueryRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static com.pickone.domain.user.entity.QUser.user;
import static com.pickone.domain.userInstrument.entity.QUserInstrument.userInstrument;
import static org.springframework.util.StringUtils.hasText;


@Repository
@RequiredArgsConstructor
public class UserQueryDslRepositoryImpl implements UserQueryRepository {

  private final JPAQueryFactory query;

  @Override
  public Page<User> searchUsers(UserSearchCondition cond, Pageable pageable) {
    BooleanBuilder where = new BooleanBuilder();

    if (hasText(cond.getKeyword())) {
      String keyword = cond.getKeyword();
      where.and(
          user.profile.nickname.containsIgnoreCase(keyword)
              .or(user.profile.email.containsIgnoreCase(keyword))
              .or(user.profile.introduction.containsIgnoreCase(keyword))
      );
    }

    if (Boolean.TRUE.equals(cond.getOnlyPublic())) {
      where.and(user.status.isPublic.isTrue());
    }

    if (cond.getGender() != null) {
      where.and(user.profile.gender.eq(cond.getGender()));
    }

    if (cond.getRole() != null) {
      where.and(user.role.eq(cond.getRole()));
    }

    if (cond.getMbti() != null) {
      where.and(user.profile.mbti.eq(cond.getMbti()));
    }

    if (cond.getInstruments() != null && !cond.getInstruments().isEmpty()) {
      where.and(userInstrument.instrument.in(cond.getInstruments()));
    }

    if (cond.getGenres() != null && !cond.getGenres().isEmpty()) {
      BooleanBuilder genreBuilder = new BooleanBuilder();
      for (var genre : cond.getGenres()) {
        genreBuilder.or(user.preference.genre1.eq(genre))
            .or(user.preference.genre2.eq(genre))
            .or(user.preference.genre3.eq(genre))
            .or(user.preference.genre4.eq(genre))
            .or(user.preference.genre5.eq(genre))
            .or(user.preference.genre6.eq(genre))
            .or(user.preference.genre7.eq(genre))
            .or(user.preference.genre8.eq(genre));
      }
      where.and(genreBuilder);
    }

    LocalDate today = LocalDate.now();
    if (cond.getMinAge() != null) {
      where.and(user.profile.birthDate.loe(today.minusYears(cond.getMinAge())));
    }
    if (cond.getMaxAge() != null) {
      where.and(user.profile.birthDate.goe(today.minusYears(cond.getMaxAge() + 1).plusDays(1)));
    }
    if (cond.getBirthDateFrom() != null) {
      where.and(user.profile.birthDate.goe(cond.getBirthDateFrom()));
    }
    if (cond.getBirthDateTo() != null) {
      where.and(user.profile.birthDate.loe(cond.getBirthDateTo()));
    }

    List<User> content = query.selectDistinct(user)
        .from(user)
        .leftJoin(user.instruments, userInstrument).fetchJoin()
        .where(where)
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    Long count = query.select(user.countDistinct())
        .from(user)
        .leftJoin(user.instruments, userInstrument)
        .where(where)
        .fetchOne();

    return PageableExecutionUtils.getPage(content, pageable, () -> count != null ? count : 0L);
  }

  @Override
  public Page<User> searchByKeywordAndPublic(String keyword, Boolean onlyPublic, Pageable pageable) {
    BooleanBuilder where = new BooleanBuilder();

    if (hasText(keyword)) {
      where.and(
          user.profile.nickname.containsIgnoreCase(keyword)
              .or(user.profile.email.containsIgnoreCase(keyword))
              .or(user.profile.introduction.containsIgnoreCase(keyword))
      );
    }

    if (Boolean.TRUE.equals(onlyPublic)) {
      where.and(user.status.isPublic.isTrue());
    }

    List<User> content = query.selectFrom(user)
        .where(where)
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    Long count = query.select(user.count())
        .from(user)
        .where(where)
        .fetchOne();

    return PageableExecutionUtils.getPage(content, pageable, () -> count != null ? count : 0L);
  }
}
