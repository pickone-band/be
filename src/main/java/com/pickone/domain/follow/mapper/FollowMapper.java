package com.pickone.domain.follow.mapper;

import com.pickone.domain.follow.dto.FollowResponse;
import com.pickone.domain.follow.entity.UserFollow;
import org.springframework.stereotype.Component;

@Component
public class FollowMapper {

  public FollowResponse toDto(UserFollow follow) {
    return toDtoWithNickname(follow, null);
  }

  public FollowResponse toDtoWithNickname(UserFollow follow, String nickname) {
    if (follow == null) return null;

    return new FollowResponse(
        follow.getId(),
        follow.getFromUser().getId(),
        follow.getToUser().getId(),
        nickname
    );
  }
}
