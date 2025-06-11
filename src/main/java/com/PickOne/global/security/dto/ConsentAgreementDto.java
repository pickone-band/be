package com.PickOne.global.security.dto;

import jakarta.validation.constraints.NotNull;

public record ConsentAgreementDto(

        @NotNull(message = "약관 ID는 필수입니다.")
        Long termId,

        @NotNull(message = "동의 여부는 필수입니다.")
        Boolean consented
) {
}
