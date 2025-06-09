package com.PickOne.domain.user.repository.impl;

import com.PickOne.domain.user.mapper.UserMapper;
import com.PickOne.domain.user.model.domain.User;
import com.PickOne.domain.user.model.entity.UserEntity;
import com.PickOne.domain.user.repository.UserJpaRepository;
import com.PickOne.domain.user.repository.UserQueryDslRepository;
import com.PickOne.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
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
    public User save(User user) {
        UserEntity saved = userJpaRepository.save(UserMapper.toEntity(user));
        return UserMapper.toDomain(saved);
    }

    @Override
    public void deleteById(Long id) {
        userJpaRepository.deleteById(id);
    }
}