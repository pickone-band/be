package com.PickOne.term.service;

import com.PickOne.term.model.domain.Terms;
import com.PickOne.term.model.domain.TermsType;
import com.PickOne.term.model.domain.Version;

import java.time.LocalDate;
import java.util.List;

/**
 * 약관 관련 비즈니스 로직을 처리하는 서비스 인터페이스
 */
public interface TermsService {

    /**
     * 새로운 약관을 생성합니다.
     *
     * @param title 약관 제목
     * @param content 약관 내용
     * @param type 약관 유형
     * @param version 약관 버전
     * @param effectiveDate 시행일
     * @param required 필수 여부
     * @return 생성된 약관
     */
    Terms createTerms(String title, String content, TermsType type, Version version,
                      LocalDate effectiveDate, boolean required);

    /**
     * ID로 약관을 조회합니다.
     *
     * @param id 약관 ID
     * @return 조회된 약관
     */
    Terms getTermsById(Long id);

    /**
     * 특정 유형의 가장 최신 버전 약관을 조회합니다.
     *
     * @param type 약관 유형
     * @return 가장 최신 버전의 약관
     */
    Terms getLatestTermsByType(TermsType type);

    /**
     * 현재 유효한 특정 유형의 약관을 조회합니다.
     *
     * @param type 약관 유형
     * @return 현재 유효한 약관
     */
    Terms getCurrentlyEffectiveTermsByType(TermsType type);

    /**
     * 필수 동의가 필요한 모든 약관을 조회합니다.
     *
     * @return 필수 동의가 필요한 약관 목록
     */
    List<Terms> getAllRequiredTerms();

    /**
     * 약관 내용을 업데이트합니다.
     *
     * @param id 약관 ID
     * @param newContent 새로운 내용
     * @return 업데이트된 약관
     */
    Terms updateTermsContent(Long id, String newContent);

    /**
     * 약관 버전을 업데이트합니다.
     *
     * @param id 약관 ID
     * @param newVersion 새로운 버전
     * @return 업데이트된 약관
     */
    Terms updateTermsVersion(Long id, String newVersion);

    /**
     * 약관 시행일을 업데이트합니다.
     *
     * @param id 약관 ID
     * @param newEffectiveDate 새로운 시행일
     * @return 업데이트된 약관
     */
    Terms updateTermsEffectiveDate(Long id, LocalDate newEffectiveDate);

    /**
     * 약관 필수 여부를 업데이트합니다.
     *
     * @param id 약관 ID
     * @param required 필수 여부
     * @return 업데이트된 약관
     */
    Terms updateTermsRequired(Long id, boolean required);

    /**
     * 향후 시행 예정인 약관을 조회합니다.
     *
     * @return 시행 예정인 약관 목록
     */
    List<Terms> getUpcomingTerms();

    /**
     * 약관을 삭제합니다. (관리자 전용)
     *
     * @param id 약관 ID
     */
    void deleteTerms(Long id);
}