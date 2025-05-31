package com.PickOne.domain.user.controller;

import com.PickOne.domain.user.dto.UserResponse;
import com.PickOne.domain.user.dto.UserUpdateRequest;
import com.PickOne.domain.user.mapper.UserMapper;
import com.PickOne.domain.user.model.domain.User;
import com.PickOne.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User API", description = "회원 정보 조회, 수정, 탈퇴 API")
public class UserController {

    private final UserService userService;

    @Operation(summary = "회원 정보 조회", description = "ID로 회원 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(UserMapper.toResponse(user));
    }

    @Operation(summary = "회원 정보 수정", description = "닉네임, 이메일, 계정 공개 여부를 수정합니다.")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @RequestBody @Valid UserUpdateRequest request) {
        User updated = userService.updateUser(id, request.toDomain(id));
        return ResponseEntity.ok(UserMapper.toResponse(updated));
    }

    @Operation(summary = "회원 탈퇴", description = "해당 사용자의 계정을 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
