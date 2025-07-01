package com.pickone.domain.user.service;

import com.pickone.domain.user.dto.UserResponseDto;
import com.pickone.domain.user.model.mapper.UserMapper;
import com.pickone.domain.user.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserQueryServiceImpl implements UserQueryService {
  private final UserJpaRepository userRepository;

  @Override
  public UserResponseDto getUser(Long userId) {
    var user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found"));
    return UserMapper.toDto(user);
  }
}
