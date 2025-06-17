package com.PickOne.domain.user.controller;

import com.PickOne.domain.user.dto.UserResponseDto;
import com.PickOne.domain.user.dto.UserSearchConditionDto;
import com.PickOne.domain.user.dto.UserUpdateRequestDto;
import com.PickOne.domain.user.model.entity.UserEntity;
import com.PickOne.domain.user.service.UserService;
import com.PickOne.global.exception.BaseResponse;
import com.PickOne.global.exception.SuccessCode;
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
    public ResponseEntity<BaseResponse<UserResponseDto>> getUserById(@PathVariable Long id) {
        UserEntity user = userService.findById(id);
        return BaseResponse.success(UserResponseDto.from(user));
    }

    @Operation(summary = "회원 정보 수정", description = "닉네임, 프로필 이미지, 악기/장르, 공개 여부를 수정합니다.")
    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> updateUser(
            @PathVariable Long id,
            @RequestBody @Valid UserUpdateRequestDto request) {
        userService.updateUser(id, request);
        return BaseResponse.success(SuccessCode.UPDATED);
    }

    @Operation(summary = "회원 탈퇴", description = "해당 사용자의 계정을 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return BaseResponse.success(SuccessCode.DELETED);
    }

    @Operation(summary = "악기로 사용자 검색", description = "악기로 사용자를 페이징 조회합니다.")
    @GetMapping("/search/instrument")
    public ResponseEntity<BaseResponse<Page<UserResponseDto>>> getUsersByInstrument(
            @RequestParam String instrument,
            Pageable pageable) {
        Page<UserEntity> users = userService.findUsersByInstrument(instrument, pageable);
        return BaseResponse.success(users.map(UserResponseDto::from));
    }

    @Operation(summary = "장르로 사용자 검색", description = "장르로 사용자를 페이징 조회합니다.")
    @GetMapping("/search/genre")
    public ResponseEntity<BaseResponse<Page<UserResponseDto>>> getUsersByGenre(
            @RequestParam String genre,
            Pageable pageable) {
        Page<UserEntity> users = userService.findUsersByGenre(genre, pageable);
        return BaseResponse.success(users.map(UserResponseDto::from));
    }

    @Operation(summary = "키워드로 사용자 검색", description = "닉네임 또는 이메일로 사용자 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<BaseResponse<Page<UserResponseDto>>> searchUsers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "true") boolean onlyPublic,
            Pageable pageable) {

        UserSearchConditionDto condition = UserSearchConditionDto.builder()
                .keyword(keyword)
                .onlyPublic(onlyPublic)
                .mbti(null)
                .instruments(null)
                .genres(null)
                .gender(null)
                .role(null)
                .minAge(null)
                .maxAge(null)
                .birthDateFrom(null)
                .birthDateTo(null)
                .build();

        Page<UserEntity> users = userService.searchUsers(condition, pageable);
        return BaseResponse.success(users.map(UserResponseDto::from));
    }
}
