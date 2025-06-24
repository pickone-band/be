package com.pickone.domain.follow.controller;

import com.pickone.domain.follow.dto.FollowRequest;
import com.pickone.domain.follow.service.FollowService;
import com.pickone.domain.user.dto.UserResponseDto;
import com.pickone.global.exception.BaseResponse;
import com.pickone.global.exception.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/follows")
@Tag(name = "Follow", description = "팔로우 관련 API")
public class FollowController {

  private final FollowService followService;

  @PostMapping
  @Operation(summary = "팔로우", description = "사용자가 다른 사용자를 팔로우합니다.")
  public ResponseEntity<BaseResponse<Void>> follow(@RequestBody FollowRequest request) {
    log.info("팔로우 요청: followerId={}, followingId={}", request.followerId(), request.followingId());
    followService.follow(request.followerId(), request.followingId());
    return BaseResponse.success(SuccessCode.CREATED);
  }

  @DeleteMapping
  @Operation(summary = "언팔로우", description = "사용자가 팔로우를 취소합니다.")
  public ResponseEntity<BaseResponse<Void>> unfollow(@RequestBody FollowRequest request) {
    log.info("언팔로우 요청: followerId={}, followingId={}", request.followerId(), request.followingId());
    followService.unfollow(request.followerId(), request.followingId());
    return BaseResponse.success(SuccessCode.DELETED);
  }

  @GetMapping("/{userId}/followers")
  public ResponseEntity<BaseResponse<List<UserResponseDto>>> getFollowers(
      @PathVariable Long userId) {
    log.info("팔로워 목록 조회 요청: userId={}", userId);
    return BaseResponse.success(
        followService.getFollowers(userId).stream().map(UserResponseDto::from).toList()
    );
  }

  @GetMapping("/{userId}/followings")
  public ResponseEntity<BaseResponse<List<UserResponseDto>>> getFollowings(
      @PathVariable Long userId) {
    log.info("팔로잉 목록 조회 요청: userId={}", userId);
    return BaseResponse.success(
        followService.getFollowings(userId).stream().map(UserResponseDto::from).toList()
    );
  }

  @GetMapping("/check")
  public ResponseEntity<BaseResponse<Boolean>> isFollowing(
      @RequestParam Long followerId,
      @RequestParam Long followingId
  ) {
    log.info("팔로우 여부 확인 요청: followerId={}, followingId={}", followerId, followingId);
    return BaseResponse.success(followService.isFollowing(followerId, followingId));
  }

  @GetMapping("/{userId}/count")
  public ResponseEntity<BaseResponse<Map<String, Integer>>> getFollowCounts(
      @PathVariable Long userId) {
    log.info("팔로우 수 조회 요청: userId={}", userId);
    int followers = followService.getFollowerCount(userId);
    int followings = followService.getFollowingCount(userId);
    return BaseResponse.success(Map.of("followers", followers, "followings", followings));
  }
}
