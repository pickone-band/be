package com.PickOne.domain.consent.controller;

import com.PickOne.domain.consent.dto.ConsentRequestDto;
import com.PickOne.domain.consent.dto.ConsentResponseDto;
import com.PickOne.domain.consent.model.entity.ConsentEntity;
import com.PickOne.domain.consent.service.ConsentService;
import com.PickOne.global.exception.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/consents")
@RequiredArgsConstructor
@Tag(name = "Consent API", description = "사용자 약관 동의 API")
public class ConsentController {

    private final ConsentService consentService;

    @Operation(summary = "동의 저장", description = "사용자의 약관 동의 여부를 저장합니다.")
    @PostMapping("/{userId}")
    public ResponseEntity<BaseResponse<ConsentResponseDto>> saveConsent(
            @PathVariable Long userId,
            @RequestBody @Valid ConsentRequestDto request
    ) {
        ConsentEntity saved = consentService.saveConsent(userId, request.termsId(), request);
        return BaseResponse.success(ConsentResponseDto.from(saved));
    }

    @Operation(summary = "사용자 동의 목록 조회", description = "특정 사용자의 전체 약관 동의 내역을 조회합니다.")
    @GetMapping("/{userId}")
    public ResponseEntity<BaseResponse<List<ConsentResponseDto>>> getUserConsents(@PathVariable Long userId) {
        List<ConsentResponseDto> responses = consentService.getUserConsents(userId).stream()
                .map(ConsentResponseDto::from)
                .toList();
        return BaseResponse.success(responses);
    }

    @Operation(summary = "특정 약관에 대한 동의 여부 확인", description = "특정 사용자와 약관 ID에 대한 동의 여부를 반환합니다.")
    @GetMapping("/{userId}/check/{termsId}")
    public ResponseEntity<BaseResponse<Boolean>> hasConsented(
            @PathVariable Long userId,
            @PathVariable Long termsId
    ) {
        boolean result = consentService.hasConsented(userId, termsId);
        return BaseResponse.success(result);
    }

    @Operation(summary = "동의 삭제", description = "특정 사용자의 특정 약관 동의 정보를 삭제합니다.")
    @DeleteMapping("/{userId}/{termsId}")
    public ResponseEntity<BaseResponse<Void>> deleteConsent(
            @PathVariable Long userId,
            @PathVariable Long termsId
    ) {
        consentService.deleteConsent(userId, termsId);
        return BaseResponse.success();
    }
}
