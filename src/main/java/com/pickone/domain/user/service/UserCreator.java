package com.pickone.domain.user.service;

import com.pickone.domain.user.dto.SignupRequestDto;
import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.domain.user.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserCreator {

  private final PasswordEncoder passwordEncoder;
  private final UserJpaRepository userRepository;

  public UserEntity createUser(SignupRequestDto dto) {
    String encodedPassword = passwordEncoder.encode(dto.password());
    UserEntity user = dto.toEntity(encodedPassword);
    return userRepository.save(user);
  }
}