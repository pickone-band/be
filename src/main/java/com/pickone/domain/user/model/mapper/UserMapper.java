package com.pickone.domain.user.model.mapper;

import com.pickone.domain.user.dto.UserPreferenceDto;
import com.pickone.domain.user.dto.UserResponseDto;
import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.domain.user.model.vo.UserPreference;

public class UserMapper {
  public static UserResponseDto toDto(UserEntity entity) {
    return new UserResponseDto(
        entity.getId(),
        entity.getProfile().getNickname(),
        entity.getProfile().getEmail(),
        entity.getProfile().getBirthDate(),
        entity.getProfile().getGender(),
        entity.getStatus().isActive(),
        entity.getStatus().isVerified(),
        toPreferenceDto(entity.getPreference())
    );
  }
  public static UserPreferenceDto toPreferenceDto(UserPreference preference) {
    if (preference == null) return null;
    return new UserPreferenceDto(
        preference.getPrimaryGenre(),
        preference.getSecondaryGenre(),
        preference.getTertiaryGenre()
    );
  }
}
