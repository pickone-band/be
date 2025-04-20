package com.PickOne.term.service;

import com.PickOne.term.model.domain.TermsType;
import com.PickOne.term.model.domain.UserConsent;

import java.util.List;

/**
 * 사용자 약관 동의 관련 비즈니스 로직을 처리하는 서비스 인터페이스
 */
public interface UserConsentService {

    /**
     * 사용자의 약관 동의 정보를 생성 또는 업데이트합니다.
     *
     * @param userId 사용자 ID
     * @param termsId 약관 ID
     * @param isConsented 동의 여부
     * @return 생성 또는 업데이트된 사용자 동의 정보
     */
    UserConsent createOrUpdateConsent(Long userId, Long termsId, boolean isConsented);

    /**
     * 사용자가 특정 약관에 동의했는지 확인합니다.
     *
     * @param userId 사용자 ID
     * @param termsId 약관 ID
     * @return 동의 여부
     */
    boolean hasUserConsented(Long userId, Long termsId);

    /**
     * 사용자가 특정 유형의 약관에 동의했는지 확인합니다.
     *
     * @param userId 사용자 ID
     * @param type 약관 유형
     * @return 동의 여부
     */
    boolean hasUserConsentedToType(Long userId, TermsType type);

    /**
     * 사용자가 필수 약관에 모두 동의했는지 확인합니다.
     *
     * @param userId 사용자 ID
     * @return 모든 필수 약관에 동의했는지 여부
     */
    boolean hasUserConsentedToAllRequiredTerms(Long userId);

    /**
     * 사용자의 모든 약관 동의 정보를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 사용자의 약관 동의 정보 목록
     */
    List<UserConsent> getAllUserConsents(Long userId);

    /**
     * 사용자가 특정 약관에 동의했는지 여부와 동의 정보를 조회합니다.
     *
     * @param userId 사용자 ID
     * @param termsId 약관 ID
     * @return 사용자의 약관 동의 정보
     */
    UserConsent getUserConsent(Long userId, Long termsId);

    /**
     * 특정 약관에 동의한 모든 사용자 ID를 조회합니다.
     *
     * @param termsId 약관 ID
     * @return 약관에 동의한 사용자 ID 목록
     */
    List<Long> getUsersConsentedToTerms(Long termsId);

    /**
     * 마케팅 정보 수신 동의 여부를 업데이트합니다.
     *
     * @param userId 사용자 ID
     * @param isConsented 동의 여부
     * @return 업데이트된 사용자 동의 정보
     */
    UserConsent updateMarketingConsent(Long userId, boolean isConsented);

    /**
     * 사용자의 마케팅 정보 수신 동의 여부를 확인합니다.
     *
     * @param userId 사용자 ID
     * @return 마케팅 정보 수신 동의 여부
     */
    boolean hasMarketingConsent(Long userId);
}