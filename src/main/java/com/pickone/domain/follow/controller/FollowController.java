package com.pickone.domain.follow.controller;

import com.pickone.domain.follow.dto.FollowRequest;
import com.pickone.domain.follow.dto.FollowResponse;
import com.pickone.domain.follow.service.FollowCommandService;
import com.pickone.domain.follow.service.FollowQueryService;
import com.pickone.global.exception.BaseResponse;
import com.pickone.global.exception.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/follow")
@RequiredArgsConstructor
@Tag(name = "Follow", description = "팔로우/언팔로우 및 팔로워/팔로잉 관련 API")
public class FollowController {

  private final FollowCommandService commandService;
  private final FollowQueryService queryService;

  @Operation(summary = "팔로우 요청", description = "특정 사용자를 팔로우하거나, 이미 팔로우 중이면 언팔로우 처리합니다.")
  @PostMapping
  public ResponseEntity<BaseResponse<FollowResponse>> follow(
      @RequestBody FollowRequest request) {
    FollowResponse response = commandService.follow(request);
    if (response == null) {
      return BaseResponse.success(SuccessCode.DELETED, null);
    }
    return BaseResponse.success(SuccessCode.CREATED, response);
  }

  @Operation(summary = "언팔로우", description = "팔로우 중인 사용자를 언팔로우합니다.")
  @DeleteMapping("/{fromUserId}/{toUserId}")
  public ResponseEntity<BaseResponse<Void>> unfollow(
      @Parameter(description = "팔로우한 사용자 ID") @PathVariable Long fromUserId,
      @Parameter(description = "언팔로우 대상 사용자 ID") @PathVariable Long toUserId) {
    commandService.unfollow(new FollowRequest(fromUserId, toUserId));
    return BaseResponse.success(SuccessCode.DELETED);
  }

  @Operation(summary = "팔로워 목록 조회", description = "해당 사용자를 팔로우 중인 사용자 목록을 조회합니다.")
  @GetMapping("/followers/{userId}")
  public ResponseEntity<BaseResponse<List<FollowResponse>>> getFollowers(
      @Parameter(description = "대상 사용자 ID") @PathVariable Long userId) {
    List<FollowResponse> followers = queryService.getFollowers(userId);
    return BaseResponse.success(followers);
  }

  @Operation(summary = "팔로잉 목록 조회", description = "해당 사용자가 팔로우 중인 사용자 목록을 조회합니다.")
  @GetMapping("/followings/{userId}")
  public ResponseEntity<BaseResponse<List<FollowResponse>>> getFollowings(
      @Parameter(description = "대상 사용자 ID") @PathVariable Long userId) {
    List<FollowResponse> followings = queryService.getFollowings(userId);
    return BaseResponse.success(followings);
  }

  @Operation(summary = "팔로우 여부 확인", description = "특정 사용자(from)가 다른 사용자(to)를 팔로우하고 있는지 확인합니다.")
  @GetMapping("/is-following")
  public ResponseEntity<BaseResponse<Boolean>> isFollowing(
      @Parameter(description = "팔로우한 사용자 ID") @RequestParam Long fromUserId,
      @Parameter(description = "팔로우 대상 사용자 ID") @RequestParam Long toUserId) {
    boolean isFollowing = queryService.isFollowing(fromUserId, toUserId);
    return BaseResponse.success(isFollowing);
  }
}
