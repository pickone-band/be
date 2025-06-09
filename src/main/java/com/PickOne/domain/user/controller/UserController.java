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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Operation(summary = "회원 정보 수정", description = "닉네임, 프로필 이미지, 악기/장르, 공개 여부를 수정합니다.")
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUser(
            @PathVariable Long id,
            @RequestBody @Valid UserUpdateRequest request) {
        userService.updateUser(id, request.toUpdatedDomain(userService.findById(id)));
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "회원 탈퇴", description = "해당 사용자의 계정을 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "악기로 사용자 검색", description = "악기로 사용자를 페이징 조회합니다.")
    @GetMapping("/search/instrument")
    public ResponseEntity<Page<UserResponse>> getUsersByInstrument(
            @RequestParam String instrument,
            Pageable pageable) {
        Page<User> users = userService.findUsersByInstrument(instrument, pageable);
        return ResponseEntity.ok(users.map(UserMapper::toResponse));
    }

    @Operation(summary = "장르로 사용자 검색", description = "장르로 사용자를 페이징 조회합니다.")
    @GetMapping("/search/genre")
    public ResponseEntity<Page<UserResponse>> getUsersByGenre(
            @RequestParam String genre,
            Pageable pageable) {
        Page<User> users = userService.findUsersByGenre(genre, pageable);
        return ResponseEntity.ok(users.map(UserMapper::toResponse));
    }

    @Operation(summary = "키워드로 사용자 검색", description = "닉네임 또는 이메일로 사용자 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<Page<UserResponse>> searchUsers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "true") boolean onlyPublic,
            Pageable pageable) {
        Page<User> users = userService.searchUsers(keyword, onlyPublic, pageable);
        return ResponseEntity.ok(users.map(UserMapper::toResponse));
    }
}
