package com.pickone.domain.follow.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "팔로우 응답")
public record FollowResponse(
    @Schema(description = "팔로우 ID", example = "10") Long id,
    @Schema(description = "팔로우 요청자 ID", example = "1") Long fromUserId,
    @Schema(description = "팔로우 대상 ID", example = "2") Long toUserId,
    @Schema(description = "닉네임", example = "johndoe") String nickname
) {}
