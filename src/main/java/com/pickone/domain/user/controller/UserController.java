package com.pickone.domain.user.controller;

import com.pickone.domain.user.dto.SignupRequestDto;
import com.pickone.domain.user.dto.UpdatePreferenceRequestDto;
import com.pickone.domain.user.dto.UserResponseDto;
import com.pickone.domain.user.dto.UpdateProfileRequestDto;
import com.pickone.domain.user.dto.ChangePasswordRequestDto;
import com.pickone.domain.user.service.UserCommandService;
import com.pickone.domain.user.service.UserQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
  private final UserCommandService commandService;
  private final UserQueryService queryService;

  @PostMapping("/signup")
  public UserResponseDto signup(@RequestBody SignupRequestDto dto) {
    return commandService.signup(dto);
  }

  @PatchMapping("/{userId}/profile")
  public void updateProfile(@PathVariable Long userId, @RequestBody UpdateProfileRequestDto dto) {
    commandService.updateProfile(userId, dto);
  }

  @PatchMapping("/{userId}/preference")
  public void updatePreference(@PathVariable Long userId, @RequestBody UpdatePreferenceRequestDto dto) {
    commandService.updatePreference(userId, dto);
  }


  @PatchMapping("/{userId}/password")
  public void changePassword(@PathVariable Long userId, @RequestBody ChangePasswordRequestDto dto) {
    commandService.changePassword(userId, dto);
  }

  @PatchMapping("/{userId}/lock")
  public void lockUser(@PathVariable Long userId) {
    commandService.lockUser(userId);
  }

  @DeleteMapping("/{userId}")
  public void deleteUser(@PathVariable Long userId) {
    commandService.deleteUser(userId);
  }

  @GetMapping("/{userId}")
  public UserResponseDto getUser(@PathVariable Long userId) {
    return queryService.getUser(userId);
  }
}
