package com.PickOne.user.service;

import com.PickOne.user.model.domain.User;

public interface UserService {
    User findById(Long id);
    User findByEmail(String email);
}