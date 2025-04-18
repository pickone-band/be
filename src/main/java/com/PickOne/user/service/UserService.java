package com.PickOne.user.service;

import com.PickOne.user.model.domain.user.User;

import java.util.List;

public interface UserService {
    User register(String email, String password);
    User findById(Long id);
    User findByEmail(String email);
    List<User> findAll();
    User changePassword(Long id, String currentPassword, String newPassword);
    void delete(Long id);
}