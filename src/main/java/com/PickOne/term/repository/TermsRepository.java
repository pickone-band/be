package com.PickOne.term.repository;

import com.PickOne.term.model.domain.Terms;
import com.PickOne.term.model.domain.TermsType;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Terms 도메인을 위한 기본 리포지토리 인터페이스
 * 구현 기술에 독립적인 메서드 정의
 */
@Repository
public interface TermsRepository {

    /**
     * 약관을 저장합니다.
     *
     * @param terms 저장할 약관
     * @return 저장된 약관
     */
    Terms save(Terms terms);

    /**
     * ID로 약관을 조회합니다.
     *
     * @param id 약관 ID
     * @return 조회된 약관 (Optional)
     */
    Optional<Terms> findById(Long id);

    /**
     * 특정 유형의 가장 최신 버전 약관을 조회합니다.
     *
     * @param type 약관 유형
     * @return 가장 최신 버전의 약관 (Optional)
     */
    Optional<Terms> findLatestByType(TermsType type);

    /**
     * 현재 유효한 특정 유형의 약관을 조회합니다.
     *
     * @param type 약관 유형
     * @param currentDate 현재 날짜
     * @return 현재 유효한 약관 (Optional)
     */
    Optional<Terms> findCurrentlyEffectiveByType(TermsType type, LocalDate currentDate);

    /**
     * 필수 동의가 필요한 모든 약관을 조회합니다.
     *
     * @param currentDate 현재 날짜
     * @return 필수 동의가 필요한 약관 목록
     */
    List<Terms> findAllRequiredAndEffective(LocalDate currentDate);

    /**
     * 약관 유형별로 모든 버전의 약관을 조회합니다.
     *
     * @param type 약관 유형
     * @return 해당 유형의 모든 버전 약관 목록
     */
    List<Terms> findAllByType(TermsType type);

    /**
     * 특정 날짜 이후에 시행되는 약관을 조회합니다.
     *
     * @param date 기준 날짜
     * @return 해당 날짜 이후에 시행되는 약관 목록
     */
    List<Terms> findAllUpcomingTerms(LocalDate date);

    /**
     * 약관 ID가 존재하는지 확인합니다.
     *
     * @param id 약관 ID
     * @return 존재 여부
     */
    boolean existsById(Long id);

    /**
     * 약관을 삭제합니다.
     *
     * @param id 삭제할 약관 ID
     */
    void deleteById(Long id);
}