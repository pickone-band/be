package com.PickOne.term.service;

import com.PickOne.term.model.domain.TermsType;
import com.PickOne.term.model.domain.UserConsent;
import com.PickOne.term.repository.userConsent.UserConsentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * UserConsentService 인터페이스의 구현체
 */
@Service
@RequiredArgsConstructor
public class UserConsentServiceImpl implements UserConsentService {

    private final UserConsentRepository userConsentRepository;
    private final TermsService termsService;


    @Override
    @Transactional
    public UserConsent createOrUpdateConsent(Long userId, Long termsId, boolean isConsented) {
        // 약관이 존재하는지 확인
        termsService.getTermsById(termsId);

        // 도메인 리포지토리를 사용해 엔티티를 찾음
        Optional<UserConsent> existingConsent = userConsentRepository.findByUserIdAndTermsId(userId, termsId);
        LocalDate now = LocalDate.now();

        if (existingConsent.isPresent()) {
            // 새 동의 객체 생성 (도메인 모델은 불변이므로)
            UserConsent updatedConsent = UserConsent.of(userId, termsId, now, isConsented);
            // 저장하면 리포지토리 구현체에서 올바른 엔티티 업데이트 처리
            return userConsentRepository.save(updatedConsent);
        } else {
            // 새 동의 생성
            UserConsent newConsent = UserConsent.of(userId, termsId, now, isConsented);
            return userConsentRepository.save(newConsent);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasUserConsented(Long userId, Long termsId) {
        return userConsentRepository.hasUserConsented(userId, termsId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasUserConsentedToType(Long userId, TermsType type) {
        try {
            // 현재 유효한 약관 조회
            Long termsId = termsService.getCurrentlyEffectiveTermsByType(type).getId();
            return userConsentRepository.hasUserConsented(userId, termsId);
        } catch (NoSuchElementException e) {
            // 해당 유형의 유효한 약관이 없는 경우
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasUserConsentedToAllRequiredTerms(Long userId) {
        // 현재 유효한 필수 약관 목록 조회
        List<Long> requiredTermsIds = termsService.getAllRequiredTerms()
                .stream()
                .map(terms -> terms.getId())
                .collect(Collectors.toList());

        if (requiredTermsIds.isEmpty()) {
            return true; // 필수 약관이 없으면 true 반환
        }

        long consentedCount = userConsentRepository.countConsentedTermsByUserIdAndTermsIds(userId, requiredTermsIds);
        return consentedCount == requiredTermsIds.size();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserConsent> getAllUserConsents(Long userId) {
        return userConsentRepository.findAllByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public UserConsent getUserConsent(Long userId, Long termsId) {
        return userConsentRepository.findByUserIdAndTermsId(userId, termsId)
                .orElseThrow(() -> new NoSuchElementException(
                        String.format("사용자 ID %d의 약관 ID %d에 대한 동의 정보가 존재하지 않습니다.", userId, termsId)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getUsersConsentedToTerms(Long termsId) {
        // 약관이 존재하는지 확인
        termsService.getTermsById(termsId);

        return userConsentRepository.findUserIdsByTermsIdAndConsented(termsId);
    }

    @Override
    @Transactional
    public UserConsent updateMarketingConsent(Long userId, boolean isConsented) {
        try {
            // 현재 유효한 마케팅 약관 조회
            Long marketingTermsId = termsService.getCurrentlyEffectiveTermsByType(TermsType.MARKETING).getId();
            return createOrUpdateConsent(userId, marketingTermsId, isConsented);
        } catch (NoSuchElementException e) {
            throw new IllegalStateException("유효한 마케팅 약관이 존재하지 않습니다.", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasMarketingConsent(Long userId) {
        return hasUserConsentedToType(userId, TermsType.MARKETING);
    }
}