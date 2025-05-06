package com.PickOne.domain.user.repository;

import com.PickOne.domain.user.model.domain.User;

import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
}