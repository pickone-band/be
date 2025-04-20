package com.PickOne.term.repository;

import com.PickOne.term.model.domain.TermsType;
import com.PickOne.term.model.entity.TermsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * JPA를 사용한 Terms 엔티티 리포지토리 인터페이스
 */
@Repository
public interface JpaTermsRepository extends JpaRepository<TermsEntity, Long> {

    /**
     * 특정 유형의 가장 최신 버전 약관을 조회합니다.
     *
     * @param type 약관 유형
     * @return 가장 최신 버전의 약관
     */
    @Query("SELECT t FROM TermsEntity t WHERE t.type = :type ORDER BY t.version DESC LIMIT 1")
    Optional<TermsEntity> findLatestByType(@Param("type") TermsType type);

    /**
     * 현재 유효한 특정 유형의 약관을 조회합니다.
     *
     * @param type 약관 유형
     * @param currentDate 현재 날짜
     * @return 현재 유효한 약관
     */
    @Query("SELECT t FROM TermsEntity t WHERE t.type = :type AND t.effectiveDate <= :currentDate ORDER BY t.effectiveDate DESC")
    Optional<TermsEntity> findCurrentlyEffectiveByType(@Param("type") TermsType type, @Param("currentDate") LocalDate currentDate);

    /**
     * 필수 동의가 필요한 모든 약관을 조회합니다.
     *
     * @param currentDate 현재 날짜
     * @return 필수 동의가 필요한 약관 목록
     */
    @Query("SELECT t FROM TermsEntity t WHERE t.required = true AND t.effectiveDate <= :currentDate ORDER BY t.type")
    List<TermsEntity> findAllRequiredAndEffective(@Param("currentDate") LocalDate currentDate);

    /**
     * 약관 유형별로 모든 버전의 약관을 조회합니다.
     *
     * @param type 약관 유형
     * @return 해당 유형의 모든 버전 약관 목록
     */
    List<TermsEntity> findAllByTypeOrderByVersionDesc(TermsType type);

    /**
     * 특정 날짜 이후에 시행되는 약관을 조회합니다.
     *
     * @param date 기준 날짜
     * @return 해당 날짜 이후에 시행되는 약관 목록
     */
    List<TermsEntity> findAllByEffectiveDateAfterOrderByEffectiveDateAsc(LocalDate date);
}