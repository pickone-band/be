package com.PickOne.domain.user.repository.impl;

import com.PickOne.domain.user.model.domain.Genre;
import com.PickOne.domain.user.model.domain.Instrument;
import com.PickOne.domain.user.model.entity.UserEntity;
import com.PickOne.domain.user.repository.UserQueryDslRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.PickOne.domain.user.model.entity.QUserEntity.userEntity;

@Repository
@RequiredArgsConstructor
public class UserQueryDslRepositoryImpl implements UserQueryDslRepository {

    private final JPAQueryFactory query;

    @Override
    public Page<UserEntity> findAllByInstrument(Instrument instrument, Pageable pageable) {
        List<UserEntity> content = query.selectFrom(userEntity)
                .where(userEntity.instruments.contains(instrument))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = query.select(userEntity.count())
                .from(userEntity)
                .where(userEntity.instruments.contains(instrument))
                .fetchOne();

        return PageableExecutionUtils.getPage(content, pageable, () -> count != null ? count : 0);
    }

    @Override
    public Page<UserEntity> findAllByGenre(Genre genre, Pageable pageable) {
        List<UserEntity> content = query.selectFrom(userEntity)
                .where(userEntity.genres.contains(genre))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = query.select(userEntity.count())
                .from(userEntity)
                .where(userEntity.genres.contains(genre))
                .fetchOne();

        return PageableExecutionUtils.getPage(content, pageable, () -> count != null ? count : 0);
    }

    @Override
    public Page<UserEntity> search(String keyword, boolean onlyPublic, Pageable pageable) {
        List<UserEntity> content = query.selectFrom(userEntity)
                .where(
                        userEntity.nickname.value.containsIgnoreCase(keyword)
                                .or(userEntity.email.value.containsIgnoreCase(keyword))
                                .and(onlyPublic ? userEntity.isPublic.isTrue() : null)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = query.select(userEntity.count())
                .from(userEntity)
                .where(
                        userEntity.nickname.value.containsIgnoreCase(keyword)
                                .or(userEntity.email.value.containsIgnoreCase(keyword))
                                .and(onlyPublic ? userEntity.isPublic.isTrue() : null)
                )
                .fetchOne();

        return PageableExecutionUtils.getPage(content, pageable, () -> count != null ? count : 0);
    }
}