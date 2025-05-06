package com.PickOne.domain.user.service;

import com.PickOne.domain.user.model.domain.User;

public interface UserService {
    User findById(Long id);
    User findByEmail(String email);
}