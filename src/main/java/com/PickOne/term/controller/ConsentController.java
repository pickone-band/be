package com.PickOne.term.controller;

import com.PickOne.global.exception.BaseResponse;
import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
import com.PickOne.global.exception.SuccessCode;
import com.PickOne.term.dto.ConsentCheckResponse;
import com.PickOne.term.dto.ConsentRequest;
import com.PickOne.term.model.domain.TermConsent;
import com.PickOne.term.service.UserConsentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/consents")
@RequiredArgsConstructor
public class ConsentController {

    private final UserConsentService userConsentService;

    // 사용자 약관 동의 생성/업데이트
    @PostMapping
    public ResponseEntity<BaseResponse<TermConsent>> saveConsent(@RequestBody ConsentRequest request) {
        Long userId = getCurrentUserId();
        TermConsent consent = TermConsent.of(
                userId,
                request.termsId(),
                LocalDateTime.now(),
                request.consented()
        );
        TermConsent savedConsent = userConsentService.saveConsent(consent);
        return BaseResponse.success(SuccessCode.CREATED, savedConsent);
    }

    // 사용자 약관 동의 여부 확인
    @GetMapping("/check/{termsId}")
    public ResponseEntity<BaseResponse<ConsentCheckResponse>> hasConsented(@PathVariable Long termsId) {
        boolean consented = userConsentService.hasUserConsented(getCurrentUserId(), termsId);
        ConsentCheckResponse response = new ConsentCheckResponse(consented);
        return BaseResponse.success(response);
    }

    // 사용자의 모든 약관 동의 정보 조회
    @GetMapping
    public ResponseEntity<BaseResponse<List<TermConsent>>> getUserConsents() {
        List<TermConsent> consents = userConsentService.getUserConsents(getCurrentUserId());
        return BaseResponse.success(consents);
    }

    // 현재 사용자 ID 가져오기
    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        // 인증 객체에서 사용자 ID 추출 (실제 구현에 맞게 수정 필요)
        try {
            return Long.parseLong(auth.getName());
        } catch (NumberFormatException e) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
        }
    }
}