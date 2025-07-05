package com.pickone.domain.user.model.mapper;

import com.pickone.domain.user.dto.UserInstrumentDto;
import com.pickone.domain.user.dto.UserPreferenceDto;
import com.pickone.domain.user.dto.UserResponseDto;
import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.domain.user.model.entity.UserInstrumentEntity;
import com.pickone.domain.user.model.vo.UserPreference;

public class UserMapper {
  public static UserResponseDto toDto(UserEntity entity) {
    return new UserResponseDto(
        entity.getId(),
        entity.getProfile().getNickname(),
        entity.getProfile().getEmail(),
        entity.getProfile().getBirthDate(),
        entity.getProfile().getGender(),
        entity.getProfile().getMbti(),
        entity.getStatus().isActive(),
        entity.getStatus().isVerified(),
        entity.getStatus().isLocked(),
        entity.getSecurityInfo().isTwoFactorEnabled(),
        toPreferenceDto(entity.getPreference()),
        entity.getInstruments().stream()
            .map(UserMapper::toInstrumentDto)
            .toList(),
        entity.getProfile().getIntroduction()
    );
  }

  public static UserInstrumentDto toInstrumentDto(UserInstrumentEntity entity) {
    return new UserInstrumentDto(
        entity.getId(),
        entity.getInstrument(),
        entity.getProficiency()
    );
  }

  public static UserPreferenceDto toPreferenceDto(UserPreference preference) {
    if (preference == null) return null;
    return new UserPreferenceDto(
        preference.getGenre1(),
        preference.getGenre2(),
        preference.getGenre3(),
        preference.getGenre4(),
        preference.getGenre5(),
        preference.getGenre6(),
        preference.getGenre7(),
        preference.getGenre8()
    );
  }
}