package com.pickone.domain.user.controller;

import com.pickone.domain.user.dto.*;
import com.pickone.domain.user.service.UserCommandService;
import com.pickone.domain.user.service.UserQueryService;
import com.pickone.global.exception.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "회원 정보 관련 API")
public class UserController {

  private final UserCommandService commandService;
  private final UserQueryService queryService;

  @Operation(summary = "회원 가입", description = "신규 사용자를 등록합니다.")
  @PostMapping("/signup")
  public ResponseEntity<BaseResponse<UserResponseDto>> signup(
      @RequestBody SignupRequestDto dto) {
    UserResponseDto response = commandService.signup(dto);
    return BaseResponse.success(response);
  }

  @Operation(summary = "프로필 수정", description = "사용자의 닉네임, 이미지 등 프로필 정보를 수정합니다.")
  @PatchMapping("/{userId}/profile")
  public ResponseEntity<BaseResponse<Void>> updateProfile(
      @Parameter(description = "사용자 ID") @PathVariable Long userId,
      @RequestBody UpdateProfileRequestDto dto) {
    commandService.updateProfile(userId, dto);
    return BaseResponse.success();
  }

  @Operation(summary = "선호 설정 변경", description = "사용자의 성향/선호 정보를 수정합니다.")
  @PatchMapping("/{userId}/preference")
  public ResponseEntity<BaseResponse<Void>> updatePreference(
      @Parameter(description = "사용자 ID") @PathVariable Long userId,
      @RequestBody UpdatePreferenceRequestDto dto) {
    commandService.updatePreference(userId, dto);
    return BaseResponse.success();
  }

  @Operation(summary = "비밀번호 변경", description = "사용자의 비밀번호를 변경합니다.")
  @PatchMapping("/{userId}/password")
  public ResponseEntity<BaseResponse<Void>> changePassword(
      @Parameter(description = "사용자 ID") @PathVariable Long userId,
      @RequestBody ChangePasswordRequestDto dto) {
    commandService.changePassword(userId, dto);
    return BaseResponse.success();
  }

  @Operation(summary = "사용자 잠금", description = "해당 사용자를 잠금 처리합니다.")
  @PatchMapping("/{userId}/lock")
  public ResponseEntity<BaseResponse<Void>> lockUser(
      @Parameter(description = "사용자 ID") @PathVariable Long userId) {
    commandService.lockUser(userId);
    return BaseResponse.success();
  }

  @Operation(summary = "회원 탈퇴", description = "해당 사용자를 탈퇴 처리(삭제)합니다.")
  @DeleteMapping("/{userId}")
  public ResponseEntity<BaseResponse<Void>> deleteUser(
      @Parameter(description = "사용자 ID") @PathVariable Long userId) {
    commandService.deleteUser(userId);
    return BaseResponse.success();
  }

  @Operation(summary = "회원 조회", description = "사용자의 상세 정보를 조회합니다.")
  @GetMapping("/{userId}")
  public ResponseEntity<BaseResponse<UserResponseDto>> getUser(
      @Parameter(description = "사용자 ID") @PathVariable Long userId) {
    UserResponseDto response = queryService.getUser(userId);
    return BaseResponse.success(response);
  }
}
