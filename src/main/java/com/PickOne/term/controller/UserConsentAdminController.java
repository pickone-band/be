package com.PickOne.term.controller;

import com.PickOne.term.controller.dto.userConsent.ConsentRequest;
import com.PickOne.term.model.domain.UserConsent;
import com.PickOne.term.service.UserConsentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 관리자용 사용자 약관 동의 관리 API를 제공하는 컨트롤러
 */
@RestController
@RequestMapping("/api/admin/user-consent")
@PreAuthorize("hasRole('ADMIN')")
public class UserConsentAdminController {

    private final UserConsentService userConsentService;

    @Autowired
    public UserConsentAdminController(UserConsentService userConsentService) {
        this.userConsentService = userConsentService;
    }

    /**
     * 관리자가 특정 사용자의 약관 동의 정보를 생성 또는 업데이트합니다.
     *
     * @param userId 사용자 ID
     * @param request 약관 동의 요청
     * @return 생성 또는 업데이트된 사용자 동의 정보
     */
    @PostMapping("/{userId}")
    public ResponseEntity<UserConsent> adminCreateOrUpdateConsent(
            @PathVariable Long userId,
            @RequestBody ConsentRequest request) {
        UserConsent userConsent = userConsentService.createOrUpdateConsent(
                userId,
                request.termsId(),
                request.isConsented()
        );
        return new ResponseEntity<>(userConsent, HttpStatus.CREATED);
    }

    /**
     * 관리자가 특정 사용자의 모든 약관 동의 정보를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 사용자의 약관 동의 정보 목록
     */
    @GetMapping("/{userId}")
    public ResponseEntity<List<UserConsent>> getAllUserConsentsForAdmin(@PathVariable Long userId) {
        List<UserConsent> consents = userConsentService.getAllUserConsents(userId);
        return ResponseEntity.ok(consents);
    }

    /**
     * 특정 약관에 동의한 모든 사용자 ID를 조회합니다.
     *
     * @param termsId 약관 ID
     * @return 약관에 동의한 사용자 ID 목록
     */
    @GetMapping("/users/{termsId}")
    public ResponseEntity<List<Long>> getUsersConsentedToTerms(@PathVariable Long termsId) {
        List<Long> userIds = userConsentService.getUsersConsentedToTerms(termsId);
        return ResponseEntity.ok(userIds);
    }

    /**
     * 관리자가 특정 사용자의 특정 약관 동의 정보를 조회합니다.
     *
     * @param userId 사용자 ID
     * @param termsId 약관 ID
     * @return 사용자의 약관 동의 정보
     */
    @GetMapping("/{userId}/{termsId}")
    public ResponseEntity<UserConsent> getUserConsentByAdmin(
            @PathVariable Long userId,
            @PathVariable Long termsId) {
        UserConsent consent = userConsentService.getUserConsent(userId, termsId);
        return ResponseEntity.ok(consent);
    }
}