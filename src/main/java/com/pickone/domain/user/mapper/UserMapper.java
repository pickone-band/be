package com.pickone.domain.user.mapper;

import com.pickone.domain.user.dto.UserPreferenceResponse;
import com.pickone.domain.user.dto.UserResponse;
import com.pickone.domain.user.entity.User;
import com.pickone.domain.user.model.vo.UserPreference;
import com.pickone.domain.userInstrument.dto.UserInstrumentResponse;
import com.pickone.domain.userInstrument.entity.UserInstrument;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {

  public UserResponse toDto(User user) {
    if (user == null) return null;

    return new UserResponse(
        user.getId(),
        user.getProfile().getNickname(),
        user.getProfile().getEmail(),
        user.getProfile().getBirthDate(),
        user.getProfile().getGender(),
        user.getProfile().getMbti(),
        user.getStatus().isActive(),
        user.getStatus().isVerified(),
        user.getStatus().isLocked(),
        user.getSecurityInfo().isTwoFactorEnabled(),
        toPreferenceDto(user.getPreference()),
        toInstrumentDtos(user.getInstruments()),
        user.getProfile().getProfileImageUrl(),
        user.getProfile().getIntroduction()
    );
  }

  public UserPreferenceResponse toPreferenceDto(UserPreference vo) {
    if (vo == null) return null;
    return new UserPreferenceResponse(
        vo.getGenre1(), vo.getGenre2(), vo.getGenre3(), vo.getGenre4(),
        vo.getGenre5(), vo.getGenre6(), vo.getGenre7(), vo.getGenre8()
    );
  }

  public List<UserInstrumentResponse> toInstrumentDtos(List<UserInstrument> entities) {
    if (entities == null) return List.of();
    return entities.stream()
        .map(e -> new UserInstrumentResponse(
            e.getId(),
            e.getInstrument(),
            e.getProficiency()
        ))
        .collect(Collectors.toList());
  }
}
