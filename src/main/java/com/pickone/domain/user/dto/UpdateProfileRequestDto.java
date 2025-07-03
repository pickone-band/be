package com.pickone.domain.user.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateProfileRequestDto(
    @NotBlank
    String nickname
) {}