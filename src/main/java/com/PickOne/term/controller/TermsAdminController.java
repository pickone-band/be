package com.PickOne.term.controller;

import com.PickOne.term.controller.dto.terms.*;
import com.PickOne.term.model.domain.Terms;
import com.PickOne.term.model.domain.Version;
import com.PickOne.term.service.TermsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 관리자용 약관 관리 API를 제공하는 컨트롤러
 */
@RestController
@RequestMapping("/api/admin/terms")
@PreAuthorize("hasRole('ADMIN')")
public class TermsAdminController {

    private final TermsService termsService;

    @Autowired
    public TermsAdminController(TermsService termsService) {
        this.termsService = termsService;
    }

    /**
     * 새로운 약관을 생성합니다.
     *
     * @param request 약관 생성 요청
     * @return 생성된 약관
     */
    @PostMapping
    public ResponseEntity<Terms> createTerms(@RequestBody CreateTermsRequest request) {
        Terms terms = termsService.createTerms(
                request.title().getValue(),
                request.content().getValue(),
                request.type(),
                request.version(),
                request.effectiveDate().getValue(),
                request.required().isValue()
        );
        return new ResponseEntity<>(terms, HttpStatus.CREATED);
    }

    /**
     * 약관 내용을 업데이트합니다.
     *
     * @param id 약관 ID
     * @param request 업데이트 요청
     * @return 업데이트된 약관
     */
    @PutMapping("/{id}/content")
    public ResponseEntity<Terms> updateTermsContent(
            @PathVariable Long id,
            @RequestBody UpdateTermsContentRequest request) {
        Terms terms = termsService.updateTermsContent(id, request.content().getValue());
        return ResponseEntity.ok(terms);
    }

    /**
     * 약관 버전을 업데이트합니다.
     *
     * @param id 약관 ID
     * @param request 업데이트 요청
     * @return 업데이트된 약관
     */
    @PutMapping("/{id}/version")
    public ResponseEntity<Terms> updateTermsVersion(
            @PathVariable Long id,
            @RequestBody UpdateTermsVersionRequest request) {
        Terms terms = termsService.updateTermsVersion(id, request.version().getValue());
        return ResponseEntity.ok(terms);
    }

    /**
     * 약관 시행일을 업데이트합니다.
     *
     * @param id 약관 ID
     * @param request 업데이트 요청
     * @return 업데이트된 약관
     */
    @PutMapping("/{id}/effectiveDate")
    public ResponseEntity<Terms> updateTermsEffectiveDate(
            @PathVariable Long id,
            @RequestBody UpdateTermsEffectiveDateRequest request) {
        Terms terms = termsService.updateTermsEffectiveDate(id, request.effectiveDate().getValue());
        return ResponseEntity.ok(terms);
    }

    /**
     * 약관 필수 여부를 업데이트합니다.
     *
     * @param id 약관 ID
     * @param request 업데이트 요청
     * @return 업데이트된 약관
     */
    @PutMapping("/{id}/required")
    public ResponseEntity<Terms> updateTermsRequired(
            @PathVariable Long id,
            @RequestBody UpdateTermsRequiredRequest request) {
        Terms terms = termsService.updateTermsRequired(id, request.required().isValue());
        return ResponseEntity.ok(terms);
    }

    /**
     * 약관을 삭제합니다.
     *
     * @param id 약관 ID
     * @return 삭제 결과
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTerms(@PathVariable Long id) {
        termsService.deleteTerms(id);
        return ResponseEntity.noContent().build();
    }
}