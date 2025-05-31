package com.PickOne.domain.user.repository;

import com.PickOne.domain.user.model.domain.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    User save(User user);
    void deleteById(Long id);

}