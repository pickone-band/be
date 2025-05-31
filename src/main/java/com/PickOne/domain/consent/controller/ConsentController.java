package com.PickOne.domain.consent.controller;

import com.PickOne.domain.consent.dto.ConsentRequestDto;
import com.PickOne.domain.consent.dto.ConsentResponseDto;
import com.PickOne.domain.consent.mapper.ConsentMapper;
import com.PickOne.domain.consent.model.domain.Consent;
import com.PickOne.domain.consent.service.ConsentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/consents")
@RequiredArgsConstructor
@Tag(name = "Consent API", description = "사용자 약관 동의 API")
public class ConsentController {

    private final ConsentService consentService;

    @Operation(summary = "동의 저장", description = "사용자의 약관 동의 여부를 저장합니다.")
    @PostMapping("/{userId}")
    public ResponseEntity<ConsentResponseDto> saveConsent(
            @PathVariable Long userId,
            @RequestBody @Valid ConsentRequestDto request
    ) {
        Consent saved = consentService.saveConsent(request.toDomain(userId));
        return ResponseEntity.ok(ConsentMapper.toResponse(saved));
    }

    @Operation(summary = "사용자 동의 목록 조회", description = "특정 사용자의 전체 약관 동의 내역을 조회합니다.")
    @GetMapping("/{userId}")
    public ResponseEntity<List<ConsentResponseDto>> getUserConsents(@PathVariable Long userId) {
        List<ConsentResponseDto> responses = consentService.getUserConsents(userId).stream()
                .map(ConsentMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "특정 약관에 대한 동의 여부 확인", description = "특정 사용자와 약관 ID에 대한 동의 여부를 반환합니다.")
    @GetMapping("/{userId}/check/{termsId}")
    public ResponseEntity<Boolean> hasConsented(
            @PathVariable Long userId,
            @PathVariable Long termsId
    ) {
        return ResponseEntity.ok(consentService.hasConsented(userId, termsId));
    }

    @Operation(summary = "동의 삭제", description = "특정 사용자의 특정 약관 동의 정보를 삭제합니다.")
    @DeleteMapping("/{userId}/{termsId}")
    public ResponseEntity<Void> deleteConsent(
            @PathVariable Long userId,
            @PathVariable Long termsId
    ) {
        consentService.deleteConsent(userId, termsId);
        return ResponseEntity.noContent().build();
    }
}
