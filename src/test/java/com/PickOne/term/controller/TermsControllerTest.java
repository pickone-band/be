package com.PickOne.term.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.PickOne.term.model.domain.*;
import com.PickOne.term.service.TermsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

/**
 * TermsController에 대한 단위 테스트 (Mockito 사용)
 */
@ExtendWith(MockitoExtension.class)
public class TermsControllerTest {

    @Mock
    private TermsService termsService;

    @InjectMocks
    private TermsController termsController;

    private Terms testTerms;
    private Terms testTerms2;
    private List<Terms> testTermsList;

    @BeforeEach
    void setUp() {
        // 테스트용 약관 객체 생성 - 팩토리 메서드 사용
        LocalDateTime now = LocalDateTime.now();

        testTerms = Terms.of(
                1L,
                Title.of("Test Terms"),
                Content.of("Test Content"),
                TermsType.SERVICE,
                Version.of("1.0.0"),
                EffectiveDate.of(LocalDate.now()),
                Required.of(true),
                now,
                now
        );

        testTerms2 = Terms.of(
                2L,
                Title.of("Privacy Policy"),
                Content.of("Privacy Content"),
                TermsType.PRIVACY,
                Version.of("1.0.0"),
                EffectiveDate.of(LocalDate.now()),
                Required.of(true),
                now,
                now
        );

        testTermsList = Arrays.asList(testTerms, testTerms2);
    }

    @Test
    @DisplayName("ID로 약관 조회 성공 테스트")
    void getTermsById_ShouldReturnTerms() {
        // Given
        given(termsService.getTermsById(anyLong())).willReturn(testTerms);

        // When
        ResponseEntity<Terms> response = termsController.getTermsById(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(testTerms.getId());
        assertThat(response.getBody().getTitle()).isEqualTo(testTerms.getTitle());
        assertThat(response.getBody().getType()).isEqualTo(testTerms.getType());
    }

    @Test
    @DisplayName("약관 유형별 최신 버전 조회 성공 테스트")
    void getLatestTermsByType_ShouldReturnLatestTerms() {
        // Given
        given(termsService.getLatestTermsByType(any(TermsType.class))).willReturn(testTerms);

        // When
        ResponseEntity<Terms> response = termsController.getLatestTermsByType(TermsType.SERVICE);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getType()).isEqualTo(TermsType.SERVICE);
        assertThat(response.getBody().getTitle()).isEqualTo(testTerms.getTitle());
    }

    @Test
    @DisplayName("현재 유효한 약관 조회 성공 테스트")
    void getCurrentlyEffectiveTermsByType_ShouldReturnCurrentTerms() {
        // Given
        given(termsService.getCurrentlyEffectiveTermsByType(any(TermsType.class))).willReturn(testTerms);

        // When
        ResponseEntity<Terms> response = termsController.getCurrentlyEffectiveTermsByType(TermsType.SERVICE);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getType()).isEqualTo(TermsType.SERVICE);
    }

    @Test
    @DisplayName("필수 약관 목록 조회 성공 테스트")
    void getAllRequiredTerms_ShouldReturnRequiredTermsList() {
        // Given
        given(termsService.getAllRequiredTerms()).willReturn(testTermsList);

        // When
        ResponseEntity<List<Terms>> response = termsController.getAllRequiredTerms();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().size()).isEqualTo(2);
        assertThat(response.getBody().get(0).getId()).isEqualTo(testTermsList.get(0).getId());
        assertThat(response.getBody().get(1).getId()).isEqualTo(testTermsList.get(1).getId());
    }

    @Test
    @DisplayName("향후 시행 예정 약관 조회 성공 테스트")
    void getUpcomingTerms_ShouldReturnUpcomingTermsList() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Terms upcomingTerms = Terms.of(
                3L,
                Title.of("Upcoming Terms"),
                Content.of("Upcoming Content"),
                TermsType.SERVICE,
                Version.of("1.1.0"),
                EffectiveDate.of(LocalDate.now().plusDays(10)),
                Required.of(true),
                now,
                now
        );

        given(termsService.getUpcomingTerms()).willReturn(Arrays.asList(upcomingTerms));

        // When
        ResponseEntity<List<Terms>> response = termsController.getUpcomingTerms();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().size()).isEqualTo(1);
        assertThat(response.getBody().get(0).getTitle().getValue()).isEqualTo("Upcoming Terms");
        assertThat(response.getBody().get(0).getEffectiveDate().getValue()).isAfter(LocalDate.now());
    }
}