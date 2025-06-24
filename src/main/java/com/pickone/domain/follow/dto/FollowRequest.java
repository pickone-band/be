package com.pickone.domain.follow.dto;

public record FollowRequest(
    Long followerId,
    Long followingId
) {

}