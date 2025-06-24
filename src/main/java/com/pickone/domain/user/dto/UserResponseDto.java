package com.pickone.domain.user.dto;

import com.pickone.domain.user.model.entity.UserEntity;

public record UserResponseDto(Long id, String email, String nickname, String profileImageUrl,
                              boolean isPublic,
                              String role) {

  public static UserResponseDto from(UserEntity user) {
    return new UserResponseDto(user.getId(), user.getEmail(), user.getNickname(),
        user.getProfileImage(), user.isPublic(), user.getRole().name());
  }
}