package com.pickone.domain.follow.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "팔로우 요청")
public record FollowRequest(
    @Schema(description = "팔로우 요청자 ID", example = "1") Long fromUserId,
    @Schema(description = "팔로우 대상 ID", example = "2") Long toUserId
) {}
