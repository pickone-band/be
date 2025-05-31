package com.PickOne.domain.user.dto;

/**
 * 회원 정보를 클라이언트에 반환하는 응답 DTO.
 */
public record UserResponse(
        Long id,
        String email,
        String nickname,
        boolean isPublic
) {}
