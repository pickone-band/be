package com.PickOne.global.security.repository;

import com.PickOne.domain.user.model.domain.User;

public interface AuthRepository{

    User save(User user);
}
