package com.PickOne.term.service;

import com.PickOne.term.model.domain.Content;
import com.PickOne.term.model.domain.Terms;
import com.PickOne.term.model.domain.TermsType;
import com.PickOne.term.model.domain.Version;
import com.PickOne.term.repository.terms.TermsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TermsServiceTest {

    @Mock
    private TermsRepository termsRepository;

    @InjectMocks
    private TermsServiceImpl termsService;

    private final Long TERMS_ID = 1L;
    private final String USER_ID = "test-user";
    private final LocalDate TODAY = LocalDate.now();

    @BeforeEach
    void setUp() {
        // 인증 정보 설정
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(USER_ID, null, "ROLE_USER"));
    }

    @Test
    @DisplayName("ID로 약관 조회 테스트")
    void getTermsById_ShouldReturnTerms() {
        // given
        Terms terms = mock(Terms.class);
        when(termsRepository.findById(TERMS_ID)).thenReturn(Optional.of(terms));

        // when
        Terms foundTerms = termsService.getTermsById(TERMS_ID);

        // then
        assertThat(foundTerms).isEqualTo(terms);
        verify(termsRepository).findById(TERMS_ID);
    }

    @Test
    @DisplayName("존재하지 않는 약관 조회 시 예외 발생 테스트")
    void getTermsById_WithNonExistingId_ShouldThrowException() {
        // given
        when(termsRepository.findById(TERMS_ID)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> termsService.getTermsById(TERMS_ID))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("약관이 존재하지 않습니다");
    }

    @Test
    @DisplayName("약관 생성 테스트")
    void createTerms_ShouldCreateAndReturnTerms() {
        // given
        String title = "서비스 이용약관";
        String content = "약관 내용입니다.";
        TermsType type = TermsType.SERVICE;
        Version version = Version.of("1.0.0");
        LocalDate effectiveDate = TODAY;
        boolean required = true;

        Terms createdTerms = mock(Terms.class);

        when(termsRepository.findAllByType(type)).thenReturn(List.of());
        when(termsRepository.save(any(Terms.class))).thenReturn(createdTerms);

        // when
        Terms result = termsService.createTerms(title, content, type, version, effectiveDate, required);

        // then
        assertThat(result).isEqualTo(createdTerms);
        verify(termsRepository).findAllByType(type);
        verify(termsRepository).save(any(Terms.class));
    }

    @Test
    @DisplayName("중복 버전 약관 생성 시 예외 발생 테스트")
    void createTerms_WithDuplicateVersion_ShouldThrowException() {
        // given
        String title = "서비스 이용약관";
        String content = "약관 내용입니다.";
        TermsType type = TermsType.SERVICE;
        Version version = Version.of("1.0.0");
        LocalDate effectiveDate = TODAY;
        boolean required = true;

        Terms existingTerms = mock(Terms.class);
        when(existingTerms.getVersionValue()).thenReturn("1.0.0");
        when(termsRepository.findAllByType(type)).thenReturn(List.of(existingTerms));

        // when & then
        assertThatThrownBy(() ->
                termsService.createTerms(title, content, type, version, effectiveDate, required))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 존재하는 약관");
    }

    @Test
    @DisplayName("약관 내용 업데이트 테스트")
    void updateTermsContent_ShouldUpdateAndReturnTerms() {
        // given
        String newContent = "새로운 약관 내용";
        Terms terms = mock(Terms.class);

        when(termsRepository.findById(TERMS_ID)).thenReturn(Optional.of(terms));
        when(terms.updateContent(any(Content.class), any(String.class))).thenReturn(terms);
        when(termsRepository.save(terms)).thenReturn(terms);

        // when
        Terms updatedTerms = termsService.updateTermsContent(TERMS_ID, newContent);

        // then
        assertThat(updatedTerms).isEqualTo(terms);
        verify(terms).updateContent(any(Content.class), any(String.class));
        verify(termsRepository).save(terms);
    }

    @Test
    @DisplayName("필수 동의 약관 목록 조회 테스트")
    void getAllRequiredTerms_ShouldReturnRequiredTermsList() {
        // given
        Terms terms1 = mock(Terms.class);
        Terms terms2 = mock(Terms.class);
        List<Terms> requiredTerms = Arrays.asList(terms1, terms2);

        when(termsRepository.findAllRequiredAndEffective(any(LocalDate.class))).thenReturn(requiredTerms);

        // when
        List<Terms> result = termsService.getAllRequiredTerms();

        // then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(terms1, terms2);
        verify(termsRepository).findAllRequiredAndEffective(any(LocalDate.class));
    }
}