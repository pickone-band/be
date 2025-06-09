package com.PickOne.domain.user.repository.impl;

import com.PickOne.domain.user.mapper.UserMapper;
import com.PickOne.domain.user.model.domain.User;
import com.PickOne.domain.user.model.entity.UserEntity;
import com.PickOne.domain.user.repository.UserJpaRepository;
import com.PickOne.domain.user.repository.UserQueryDslRepository;
import com.PickOne.domain.user.repository.UserRepository;
import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaUserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;
    private final UserQueryDslRepository userQueryDslRepository;

    @Override
    public Optional<User> findById(Long id) {
        return userJpaRepository.findById(id).map(UserMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email).map(UserMapper::toDomain);
    }

    public Page<User> findAllByInstrument(String instrument, Pageable pageable) {
        return userQueryDslRepository.findAllByInstrument(instrument, pageable).map(UserMapper::toDomain);
    }

    public Page<User> findAllByGenre(String genre, Pageable pageable) {
        return userQueryDslRepository.findAllByGenre(genre, pageable).map(UserMapper::toDomain);
    }

    public Page<User> search(String keyword, boolean onlyPublic, Pageable pageable) {
        return userQueryDslRepository.search(keyword, onlyPublic, pageable).map(UserMapper::toDomain);
    }
    @Override
    public void update(User user) {
        UserEntity entity = userJpaRepository.findById(user.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));

        entity.updateFromDomain(user);
    }

    @Override
    public void deleteById(Long id) {
        userJpaRepository.deleteById(id);
    }
}