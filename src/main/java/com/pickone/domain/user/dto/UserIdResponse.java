package com.pickone.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자 ID 및 닉네임 응답")
public record UserIdResponse(
    @Schema(description = "사용자 ID", example = "42")
    Long id,

    @Schema(description = "사용자 닉네임", example = "홍길동")
    String nickname
) {}
