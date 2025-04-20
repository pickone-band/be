package com.PickOne.term.repository;

import com.PickOne.term.model.entity.UserConsentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA를 사용한 UserConsent 엔티티 리포지토리 인터페이스
 */
@Repository
public interface JpaUserConsentRepository extends JpaRepository<UserConsentEntity, Long> {

    /**
     * 사용자 ID와 약관 ID로 동의 정보를 조회합니다.
     *
     * @param userId 사용자 ID
     * @param termsId 약관 ID
     * @return 사용자의 약관 동의 정보 (Optional)
     */
    Optional<UserConsentEntity> findByUserIdAndTermsId(Long userId, Long termsId);

    /**
     * 사용자 ID로 모든 동의 정보를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 사용자의 모든 약관 동의 정보 목록
     */
    List<UserConsentEntity> findAllByUserId(Long userId);

    /**
     * 약관 ID로 모든 동의 정보를 조회합니다.
     *
     * @param termsId 약관 ID
     * @return 해당 약관에 대한 모든 사용자 동의 정보 목록
     */
    List<UserConsentEntity> findAllByTermsId(Long termsId);

    /**
     * 사용자가 특정 약관에 동의했는지 여부를 확인합니다.
     *
     * @param userId 사용자 ID
     * @param termsId 약관 ID
     * @return 동의 여부 (true/false)
     */
    @Query("SELECT CASE WHEN COUNT(uc) > 0 THEN TRUE ELSE FALSE END FROM UserConsentEntity uc WHERE uc.userId = :userId AND uc.termsId = :termsId AND uc.isConsented = true")
    boolean hasUserConsented(@Param("userId") Long userId, @Param("termsId") Long termsId);

    /**
     * 특정 약관 ID 집합에 대해 사용자가 모두 동의했는지 확인합니다.
     *
     * @param userId 사용자 ID
     * @param termsIds 약관 ID 목록
     * @return 동의한 약관 수
     */
    @Query("SELECT COUNT(uc) FROM UserConsentEntity uc WHERE uc.userId = :userId AND uc.termsId IN :termsIds AND uc.isConsented = true")
    long countConsentedTermsByUserIdAndTermsIds(@Param("userId") Long userId, @Param("termsIds") List<Long> termsIds);

    /**
     * 특정 약관에 동의한 모든 사용자 ID를 조회합니다.
     *
     * @param termsId 약관 ID
     * @return 약관에 동의한 사용자 ID 목록
     */
    @Query("SELECT uc.userId FROM UserConsentEntity uc WHERE uc.termsId = :termsId AND uc.isConsented = true")
    List<Long> findUserIdsByTermsIdAndConsented(@Param("termsId") Long termsId);
}