package com.pickone.domain.user.service;

import com.pickone.domain.user.dto.UserResponseDto;

public interface UserQueryService {
  UserResponseDto getUser(Long userId);
}
