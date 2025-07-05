package com.pickone.domain.user.controller;

import com.pickone.domain.user.dto.*;
import com.pickone.domain.user.service.UserCommandService;
import com.pickone.domain.user.service.UserQueryService;
import com.pickone.global.exception.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserCommandService commandService;
  private final UserQueryService queryService;

  @PostMapping("/signup")
  public ResponseEntity<BaseResponse<UserResponseDto>> signup(@RequestBody SignupRequestDto dto) {
    UserResponseDto response = commandService.signup(dto);
    return BaseResponse.success(response);
  }

  @PatchMapping("/{userId}/profile")
  public ResponseEntity<BaseResponse<Void>> updateProfile(@PathVariable Long userId,
      @RequestBody UpdateProfileRequestDto dto) {
    commandService.updateProfile(userId, dto);
    return BaseResponse.success();
  }

  @PatchMapping("/{userId}/preference")
  public ResponseEntity<BaseResponse<Void>> updatePreference(@PathVariable Long userId,
      @RequestBody UpdatePreferenceRequestDto dto) {
    commandService.updatePreference(userId, dto);
    return BaseResponse.success();
  }

  @PatchMapping("/{userId}/password")
  public ResponseEntity<BaseResponse<Void>> changePassword(@PathVariable Long userId,
      @RequestBody ChangePasswordRequestDto dto) {
    commandService.changePassword(userId, dto);
    return BaseResponse.success();
  }

  @PatchMapping("/{userId}/lock")
  public ResponseEntity<BaseResponse<Void>> lockUser(@PathVariable Long userId) {
    commandService.lockUser(userId);
    return BaseResponse.success();
  }

  @DeleteMapping("/{userId}")
  public ResponseEntity<BaseResponse<Void>> deleteUser(@PathVariable Long userId) {
    commandService.deleteUser(userId);
    return BaseResponse.success();
  }

 
}
