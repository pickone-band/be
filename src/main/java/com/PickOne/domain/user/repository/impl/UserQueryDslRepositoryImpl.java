package com.PickOne.domain.user.repository.impl;

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
    public Page<UserEntity> findAllByInstrument(String instrumentName, Pageable pageable) {
        List<UserEntity> content = query.selectFrom(userEntity)
                .where(userEntity.instruments.any().name.eq(instrumentName))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = query.select(userEntity.count())
                .from(userEntity)
                .where(userEntity.instruments.any().name.eq(instrumentName))
                .fetchOne();

        return PageableExecutionUtils.getPage(content, pageable, () -> count != null ? count : 0);
    }

    @Override
    public Page<UserEntity> findAllByGenre(String genreName, Pageable pageable) {
        List<UserEntity> content = query.selectFrom(userEntity)
                .where(userEntity.genres.any().name.eq(genreName))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = query.select(userEntity.count())
                .from(userEntity)
                .where(userEntity.genres.any().name.eq(genreName))
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