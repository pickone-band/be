package com.PickOne.term.controller;

import com.PickOne.global.exception.BaseResponse;
import com.PickOne.global.exception.SuccessCode;
import com.PickOne.term.dto.TermsRequest;
import com.PickOne.term.model.domain.Term;
import com.PickOne.term.service.TermService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/terms")
@RequiredArgsConstructor
public class TermController {

    private final TermService termsService;

    // 약관 생성 (관리자 전용)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<Term>> createTerms(@RequestBody TermsRequest request) {
        Term term = Term.create(
                request.title(),
                request.content(),
                request.type(),
                request.version(),
                request.effectiveDate(),
                request.required()
        );
        Term createdTerm = termsService.createTerms(term);
        return BaseResponse.success(SuccessCode.CREATED, createdTerm);
    }

    // ID로 약관 조회
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<Term>> getTermsById(@PathVariable Long id) {
        Term term = termsService.getTermsById(id);
        return BaseResponse.success(term);
    }

    // 필수 약관 목록 조회
    @GetMapping("/required")
    public ResponseEntity<BaseResponse<List<Term>>> getRequiredTerms() {
        List<Term> requiredTerms = termsService.getRequiredTerms();
        return BaseResponse.success(requiredTerms);
    }

    // 약관 삭제 (관리자 전용)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BaseResponse<Void>> deleteTerms(@PathVariable Long id) {
        termsService.deleteById(id);
        return BaseResponse.success(SuccessCode.DELETED);
    }
}