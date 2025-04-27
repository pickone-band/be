package com.PickOne.term.controller;

import com.PickOne.term.controller.dto.userConsent.ConsentCheckResponse;
import com.PickOne.term.controller.dto.userConsent.ConsentRequest;
import com.PickOne.term.controller.dto.userConsent.MarketingConsentRequest;
import com.PickOne.term.model.domain.TermsType;
import com.PickOne.term.model.domain.UserConsent;
import com.PickOne.term.service.UserConsentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 일반 사용자용 약관 동의 API를 제공하는 컨트롤러
 */
@RestController
@RequestMapping("/api/user-consent")
public class UserConsentController {

    private final UserConsentService userConsentService;

    @Autowired
    public UserConsentController(UserConsentService userConsentService) {
        this.userConsentService = userConsentService;
    }

    /**
     * 현재 인증된 사용자의 약관 동의 정보를 생성 또는 업데이트합니다.
     *
     * @param request 약관 동의 요청
     * @return 생성 또는 업데이트된 사용자 동의 정보
     */
    @PostMapping
    public ResponseEntity<UserConsent> createOrUpdateConsent(@RequestBody ConsentRequest request) {
        Long userId = getCurrentUserId();
        UserConsent userConsent = userConsentService.createOrUpdateConsent(
                userId,
                request.termsId(),
                request.isConsented()
        );
        return new ResponseEntity<>(userConsent, HttpStatus.CREATED);
    }

    /**
     * 현재 인증된 사용자가 특정 약관에 동의했는지 확인합니다.
     *
     * @param termsId 약관 ID
     * @return 동의 여부
     */
    @GetMapping("/check/{termsId}")
    public ResponseEntity<ConsentCheckResponse> hasUserConsented(@PathVariable Long termsId) {
        Long userId = getCurrentUserId();
        boolean hasConsented = userConsentService.hasUserConsented(userId, termsId);
        return ResponseEntity.ok(new ConsentCheckResponse(hasConsented));
    }

    /**
     * 현재 인증된 사용자가 특정 유형의 약관에 동의했는지 확인합니다.
     *
     * @param type 약관 유형
     * @return 동의 여부
     */
    @GetMapping("/check/type")
    public ResponseEntity<ConsentCheckResponse> hasUserConsentedToType(@RequestParam TermsType type) {
        Long userId = getCurrentUserId();
        boolean hasConsented = userConsentService.hasUserConsentedToType(userId, type);
        return ResponseEntity.ok(new ConsentCheckResponse(hasConsented));
    }

    /**
     * 현재 인증된 사용자가 필수 약관에 모두 동의했는지 확인합니다.
     *
     * @return 모든 필수 약관에 동의했는지 여부
     */
    @GetMapping("/check/required")
    public ResponseEntity<ConsentCheckResponse> hasUserConsentedToAllRequiredTerms() {
        Long userId = getCurrentUserId();
        boolean hasConsented = userConsentService.hasUserConsentedToAllRequiredTerms(userId);
        return ResponseEntity.ok(new ConsentCheckResponse(hasConsented));
    }

    /**
     * 현재 인증된 사용자의 모든 약관 동의 정보를 조회합니다.
     *
     * @return 사용자의 약관 동의 정보 목록
     */
    @GetMapping
    public ResponseEntity<List<UserConsent>> getAllUserConsents() {
        Long userId = getCurrentUserId();
        List<UserConsent> consents = userConsentService.getAllUserConsents(userId);
        return ResponseEntity.ok(consents);
    }

    /**
     * 현재 인증된 사용자의 특정 약관 동의 정보를 조회합니다.
     *
     * @param termsId 약관 ID
     * @return 사용자의 약관 동의 정보
     */
    @GetMapping("/{termsId}")
    public ResponseEntity<UserConsent> getUserConsent(@PathVariable Long termsId) {
        Long userId = getCurrentUserId();
        UserConsent consent = userConsentService.getUserConsent(userId, termsId);
        return ResponseEntity.ok(consent);
    }

    /**
     * 현재 인증된 사용자의 마케팅 정보 수신 동의 여부를 업데이트합니다.
     *
     * @param request 마케팅 동의 요청
     * @return 업데이트된 사용자 동의 정보
     */
    @PostMapping("/marketing")
    public ResponseEntity<UserConsent> updateMarketingConsent(@RequestBody MarketingConsentRequest request) {
        Long userId = getCurrentUserId();
        UserConsent userConsent = userConsentService.updateMarketingConsent(userId, request.isConsented());
        return ResponseEntity.ok(userConsent);
    }

    /**
     * 현재 인증된 사용자의 마케팅 정보 수신 동의 여부를 확인합니다.
     *
     * @return 마케팅 정보 수신 동의 여부
     */
    @GetMapping("/marketing")
    public ResponseEntity<ConsentCheckResponse> hasMarketingConsent() {
        Long userId = getCurrentUserId();
        boolean hasConsented = userConsentService.hasMarketingConsent(userId);
        return ResponseEntity.ok(new ConsentCheckResponse(hasConsented));
    }

    /**
     * 현재 인증된 사용자의 ID를 가져옵니다.
     * 참고: SecurityContext에서 사용자 ID를 가져오는 임시 구현
     * 실제 구현에서는 보다 안전한 방식으로 변경되어야 합니다.
     *
     * @return 현재 인증된 사용자의 ID
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("인증된 사용자가 없습니다.");
        }

        // 테스트에서는 Principal을 Long 타입으로 직접 설정하여 사용
        if (authentication.getPrincipal() instanceof Long) {
            return (Long) authentication.getPrincipal();
        }

        // 실제 환경에서는 사용자 정보에서 ID를 추출
        // 여기서는 간단히 사용자 이름을 ID로 변환하는 임시 구현
        try {
            String username = authentication.getName();
            if (username.startsWith("user")) {
                return Long.parseLong(username.substring(4));
            }
            throw new IllegalStateException("사용자 ID를 식별할 수 없습니다.");
        } catch (NumberFormatException e) {
            throw new IllegalStateException("사용자 ID 변환 중 오류가 발생했습니다.", e);
        }
    }
}