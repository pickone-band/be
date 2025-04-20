package com.PickOne.term.controller;

import com.PickOne.term.model.domain.Terms;
import com.PickOne.term.model.domain.TermsType;
import com.PickOne.term.service.TermsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 일반 사용자용 약관 조회 API를 제공하는 컨트롤러
 */
@RestController
@RequestMapping("/api/terms")
public class TermsController {

    private final TermsService termsService;

    @Autowired
    public TermsController(TermsService termsService) {
        this.termsService = termsService;
    }

    /**
     * ID로 약관을 조회합니다.
     *
     * @param id 약관 ID
     * @return 조회된 약관
     */
    @GetMapping("/{id}")
    public ResponseEntity<Terms> getTermsById(@PathVariable Long id) {
        Terms terms = termsService.getTermsById(id);
        return ResponseEntity.ok(terms);
    }

    /**
     * 특정 유형의 가장 최신 버전 약관을 조회합니다.
     *
     * @param type 약관 유형
     * @return 가장 최신 버전의 약관
     */
    @GetMapping("/latest")
    public ResponseEntity<Terms> getLatestTermsByType(@RequestParam TermsType type) {
        Terms terms = termsService.getLatestTermsByType(type);
        return ResponseEntity.ok(terms);
    }

    /**
     * 현재 유효한 특정 유형의 약관을 조회합니다.
     *
     * @param type 약관 유형
     * @return 현재 유효한 약관
     */
    @GetMapping("/current")
    public ResponseEntity<Terms> getCurrentlyEffectiveTermsByType(@RequestParam TermsType type) {
        Terms terms = termsService.getCurrentlyEffectiveTermsByType(type);
        return ResponseEntity.ok(terms);
    }

    /**
     * 필수 동의가 필요한 모든 약관을 조회합니다.
     *
     * @return 필수 동의가 필요한 약관 목록
     */
    @GetMapping("/required")
    public ResponseEntity<List<Terms>> getAllRequiredTerms() {
        List<Terms> requiredTerms = termsService.getAllRequiredTerms();
        return ResponseEntity.ok(requiredTerms);
    }

    /**
     * 향후 시행 예정인 약관을 조회합니다.
     *
     * @return 시행 예정인 약관 목록
     */
    @GetMapping("/upcoming")
    public ResponseEntity<List<Terms>> getUpcomingTerms() {
        List<Terms> upcomingTerms = termsService.getUpcomingTerms();
        return ResponseEntity.ok(upcomingTerms);
    }
}