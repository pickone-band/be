package com.pickone.domain.user.service;

import com.pickone.domain.user.dto.UserResponse;

public interface UserQueryService {
  UserResponse getUser(Long userId);
}
