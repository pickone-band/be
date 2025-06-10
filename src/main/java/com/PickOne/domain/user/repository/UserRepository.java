package com.PickOne.domain.user.repository;

import com.PickOne.domain.user.model.domain.Email;
import com.PickOne.domain.user.model.domain.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(Long id);
    Optional<User> findByEmail(Email email);
    void deleteById(Long id);
    void update(User user);
}