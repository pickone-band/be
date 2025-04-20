package com.PickOne.term.repository;

import com.PickOne.term.model.domain.UserConsent;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * UserConsent 도메인을 위한 기본 리포지토리 인터페이스
 * 구현 기술에 독립적인 메서드 정의
 */
@Repository
public interface UserConsentRepository {

    /**
     * 사용자 동의 정보를 저장합니다.
     *
     * @param userConsent 저장할 사용자 동의 정보
     * @return 저장된 사용자 동의 정보
     */
    UserConsent save(UserConsent userConsent);

    /**
     * 사용자 ID와 약관 ID로 동의 정보를 조회합니다.
     *
     * @param userId 사용자 ID
     * @param termsId 약관 ID
     * @return 사용자의 약관 동의 정보 (Optional)
     */
    Optional<UserConsent> findByUserIdAndTermsId(Long userId, Long termsId);

    /**
     * 사용자 ID로 모든 동의 정보를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 사용자의 모든 약관 동의 정보 목록
     */
    List<UserConsent> findAllByUserId(Long userId);

    /**
     * 약관 ID로 모든 동의 정보를 조회합니다.
     *
     * @param termsId 약관 ID
     * @return 해당 약관에 대한 모든 사용자 동의 정보 목록
     */
    List<UserConsent> findAllByTermsId(Long termsId);

    /**
     * 사용자가 특정 약관에 동의했는지 여부를 확인합니다.
     *
     * @param userId 사용자 ID
     * @param termsId 약관 ID
     * @return 동의 여부 (true/false)
     */
    boolean hasUserConsented(Long userId, Long termsId);

    /**
     * 특정 약관 ID 집합에 대해 사용자가 동의한 수를 계산합니다.
     *
     * @param userId 사용자 ID
     * @param termsIds 약관 ID 목록
     * @return 동의한 약관 수
     */
    long countConsentedTermsByUserIdAndTermsIds(Long userId, List<Long> termsIds);

    /**
     * 특정 약관에 동의한 모든 사용자 ID를 조회합니다.
     *
     * @param termsId 약관 ID
     * @return 약관에 동의한 사용자 ID 목록
     */
    List<Long> findUserIdsByTermsIdAndConsented(Long termsId);
}