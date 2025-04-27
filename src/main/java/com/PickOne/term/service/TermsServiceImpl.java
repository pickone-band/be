package com.PickOne.term.service;

import com.PickOne.term.model.domain.*;
import com.PickOne.term.repository.terms.TermsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * TermsService 인터페이스의 구현체
 */
@Service
@RequiredArgsConstructor
public class TermsServiceImpl implements TermsService {

    private final TermsRepository termsRepository;

    @Override
    @Transactional
    public Terms createTerms(String title, String content, TermsType type, Version version,
                             LocalDate effectiveDate, boolean required) {

        // 기존에 같은 유형, 같은 버전의 약관이 있는지 검증
        List<Terms> existingTerms = termsRepository.findAllByType(type);
        for (Terms terms : existingTerms) {
            if (terms.getVersionValue().equals(version.getValue())) {
                throw new IllegalArgumentException(
                        String.format("이미 존재하는 약관 유형(%s)과 버전(%s)입니다.", type.getValue(), version));
            }
        }

        String currentUserId = getCurrentUserId();

        Terms newTerms = Terms.create(
                Title.of(title),
                Content.of(content),
                type,
                version,
                EffectiveDate.of(effectiveDate),
                Required.of(required)
        );

        return termsRepository.save(newTerms);
    }

    @Override
    @Transactional(readOnly = true)
    public Terms getTermsById(Long id) {
        return termsRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("ID가 " + id + "인 약관이 존재하지 않습니다."));
    }

    @Override
    @Transactional(readOnly = true)
    public Terms getLatestTermsByType(TermsType type) {
        return termsRepository.findLatestByType(type)
                .orElseThrow(() -> new NoSuchElementException("유형이 " + type.getValue() + "인 약관이 존재하지 않습니다."));
    }

    @Override
    @Transactional(readOnly = true)
    public Terms getCurrentlyEffectiveTermsByType(TermsType type) {
        return termsRepository.findCurrentlyEffectiveByType(type, LocalDate.now())
                .orElseThrow(() -> new NoSuchElementException("유형이 " + type.getValue() + "인 유효한 약관이 존재하지 않습니다."));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Terms> getAllRequiredTerms() {
        return termsRepository.findAllRequiredAndEffective(LocalDate.now());
    }

    @Override
    @Transactional
    public Terms updateTermsContent(Long id, String newContent) {
        Terms terms = getTermsById(id);
        terms.updateContent(Content.of(newContent), getCurrentUserId());
        return termsRepository.save(terms);
    }

    @Override
    @Transactional
    public Terms updateTermsVersion(Long id, String newVersion) {
        Terms terms = getTermsById(id);

        // 같은 유형의 다른 약관 중에 동일한 버전이 있는지 확인
        List<Terms> existingTerms = termsRepository.findAllByType(terms.getType());
        for (Terms other : existingTerms) {
            if (other.getId().equals(id)) {
                continue;  // 현재 약관은 건너뜀
            }
            if (other.getVersionValue().equals(newVersion)) {
                throw new IllegalArgumentException(
                        String.format("이미 존재하는 약관 버전(%s)입니다.", newVersion));
            }
        }

        terms.updateVersion(Version.of(newVersion), getCurrentUserId());
        return termsRepository.save(terms);
    }

    @Override
    @Transactional
    public Terms updateTermsEffectiveDate(Long id, LocalDate newEffectiveDate) {
        Terms terms = getTermsById(id);
        terms.updateEffectiveDate(EffectiveDate.of(newEffectiveDate), getCurrentUserId());
        return termsRepository.save(terms);
    }

    @Override
    @Transactional
    public Terms updateTermsRequired(Long id, boolean required) {
        Terms terms = getTermsById(id);
        terms.updateRequired(Required.of(required), getCurrentUserId());
        return termsRepository.save(terms);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Terms> getUpcomingTerms() {
        return termsRepository.findAllUpcomingTerms(LocalDate.now());
    }

    @Override
    @Transactional
    public void deleteTerms(Long id) {
        // 약관이 존재하는지 확인
        if (!termsRepository.existsById(id)) {
            throw new NoSuchElementException("ID가 " + id + "인 약관이 존재하지 않습니다.");
        }
        termsRepository.deleteById(id);
    }

    /**
     * 현재 인증된 사용자의 ID를 가져옵니다.
     *
     * @return 현재 사용자 ID
     */
    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "anonymous";
        }
        return authentication.getName();
    }
}