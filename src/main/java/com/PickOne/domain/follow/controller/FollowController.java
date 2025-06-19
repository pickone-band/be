package com.PickOne.domain.follow.controller;

import com.PickOne.domain.follow.dto.FollowRequest;
import com.PickOne.domain.follow.service.FollowService;
import com.PickOne.domain.user.dto.UserResponseDto;
import com.PickOne.domain.user.model.entity.UserEntity;
import com.PickOne.global.exception.BaseResponse;
import com.PickOne.global.exception.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Follow", description = "팔로우 관련 API")
@RestController
@RequestMapping("/api/follows")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @Operation(summary = "팔로우", description = "사용자가 다른 사용자를 팔로우합니다.")
    @PostMapping
    public ResponseEntity<BaseResponse<Void>> follow(
            @RequestBody FollowRequest request
    ) {
        followService.follow(request.followerId(), request.followingId());
        return BaseResponse.success(SuccessCode.CREATED);
    }

    @Operation(summary = "언팔로우", description = "사용자가 팔로우를 취소합니다.")
    @DeleteMapping
    public ResponseEntity<BaseResponse<Void>> unfollow(
            @RequestBody FollowRequest request
    ) {
        followService.unfollow(request.followerId(), request.followingId());
        return BaseResponse.success(SuccessCode.DELETED);
    }

    @Operation(summary = "팔로워 목록 조회", description = "해당 사용자의 팔로워 목록을 조회합니다.")
    @GetMapping("/{userId}/followers")
    public ResponseEntity<BaseResponse<List<UserResponseDto>>> getFollowers(
            @PathVariable Long userId
    ) {
        List<UserEntity> followers = followService.getFollowers(userId);
        return BaseResponse.success(followers.stream().map(UserResponseDto::from).toList());
    }

    @Operation(summary = "팔로잉 목록 조회", description = "해당 사용자가 팔로우 중인 사용자 목록을 조회합니다.")
    @GetMapping("/{userId}/followings")
    public ResponseEntity<BaseResponse<List<UserResponseDto>>> getFollowings(
            @PathVariable Long userId
    ) {
        List<UserEntity> followings = followService.getFollowings(userId);
        return BaseResponse.success(followings.stream().map(UserResponseDto::from).toList());
    }

    @Operation(summary = "팔로우 여부 확인", description = "사용자 A가 사용자 B를 팔로우하고 있는지 여부를 확인합니다.")
    @GetMapping("/check")
    public ResponseEntity<BaseResponse<Boolean>> isFollowing(
            @RequestParam Long followerId,
            @RequestParam Long followingId
    ) {
        boolean result = followService.isFollowing(followerId, followingId);
        return BaseResponse.success(result);
    }

    @Operation(summary = "팔로우 수 조회", description = "사용자의 팔로워 수와 팔로잉 수를 반환합니다.")
    @GetMapping("/{userId}/count")
    public ResponseEntity<BaseResponse<Map<String, Integer>>> getFollowCounts(@PathVariable Long userId) {
        int followers = followService.getFollowerCount(userId);
        int followings = followService.getFollowingCount(userId);
        Map<String, Integer> result = Map.of("followers", followers, "followings", followings);
        return BaseResponse.success(result);
    }

}
