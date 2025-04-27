package com.PickOne.term.controller;

import com.PickOne.term.controller.dto.terms.*;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

/**
 * TermsAdminController에 대한 단위 테스트 (Mockito 사용)
 */
@ExtendWith(MockitoExtension.class)
public class TermsAdminControllerTest {

    @Mock
    private TermsService termsService;

    @InjectMocks
    private TermsAdminController termsAdminController;

    private Terms testTerms;
    private CreateTermsRequest createRequest;

    @BeforeEach
    void setUp() {
        // 테스트용 약관 객체 생성 - 팩토리 메서드 사용
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = LocalDate.now();

        testTerms = Terms.of(
                1L,
                Title.of("Test Terms"),
                Content.of("Test Content"),
                TermsType.SERVICE,
                Version.of("1.0.0"),
                EffectiveDate.of(today),
                Required.of(true),
                now,
                now
        );

        createRequest = new CreateTermsRequest(
                Title.of("Test Terms"), Content.of("Test Content"), TermsType.SERVICE, Version.of("1.0.0"), EffectiveDate.of(today), Required.of(true));
    }

    @Test
    @DisplayName("약관 생성 성공 테스트")
    void createTerms_ShouldReturnCreatedTerms() {
        // Given
        given(termsService.createTerms(
                anyString(), anyString(), any(TermsType.class), any(Version.class), any(LocalDate.class), anyBoolean()))
                .willReturn(testTerms);

        // When
        ResponseEntity<Terms> response = termsAdminController.createTerms(createRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(testTerms.getId());
        assertThat(response.getBody().getTitle().getValue()).isEqualTo(testTerms.getTitle().getValue());
        assertThat(response.getBody().getContent().getValue()).isEqualTo(testTerms.getContent().getValue());
    }

    @Test
    @DisplayName("약관 내용 업데이트 성공 테스트")
    void updateTermsContent_ShouldReturnUpdatedTerms() {
        // Given
        UpdateTermsContentRequest request = new UpdateTermsContentRequest(Content.of("Updated Content"));

        // 내용이 업데이트된 약관 객체 생성
        Terms updatedTerms = Terms.of(
                testTerms.getId(),
                testTerms.getTitle(),
                Content.of("Updated Content"),
                testTerms.getType(),
                testTerms.getVersion(),
                testTerms.getEffectiveDate(),
                testTerms.getRequired(),
                testTerms.getCreatedAt(),
                LocalDateTime.now() // 업데이트 시간은 현재 시간으로
        );

        given(termsService.updateTermsContent(anyLong(), anyString())).willReturn(updatedTerms);

        // When
        ResponseEntity<Terms> response = termsAdminController.updateTermsContent(1L, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getContent().getValue()).isEqualTo("Updated Content");
        verify(termsService).updateTermsContent(1L, "Updated Content");
    }

    @Test
    @DisplayName("약관 버전 업데이트 성공 테스트")
    void updateTermsVersion_ShouldReturnUpdatedTerms() {
        // Given
        UpdateTermsVersionRequest request = new UpdateTermsVersionRequest(Version.of("1.0.1"));

        // 버전이 업데이트된 약관 객체 생성
        Terms updatedTerms = Terms.of(
                testTerms.getId(),
                testTerms.getTitle(),
                testTerms.getContent(),
                testTerms.getType(),
                Version.of("1.0.1"),
                testTerms.getEffectiveDate(),
                testTerms.getRequired(),
                testTerms.getCreatedAt(),
                LocalDateTime.now() // 업데이트 시간은 현재 시간으로
        );

        given(termsService.updateTermsVersion(anyLong(), anyString())).willReturn(updatedTerms);

        // When
        ResponseEntity<Terms> response = termsAdminController.updateTermsVersion(1L, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getVersion().getValue()).isEqualTo("1.0.1");
        verify(termsService).updateTermsVersion(1L, "1.0.1");
    }

    @Test
    @DisplayName("약관 시행일 업데이트 성공 테스트")
    void updateTermsEffectiveDate_ShouldReturnUpdatedTerms() {
        // Given
        LocalDate newDate = LocalDate.now().plusDays(10);
        UpdateTermsEffectiveDateRequest request = new UpdateTermsEffectiveDateRequest(EffectiveDate.of(newDate));

        // 시행일이 업데이트된 약관 객체 생성
        Terms updatedTerms = Terms.of(
                testTerms.getId(),
                testTerms.getTitle(),
                testTerms.getContent(),
                testTerms.getType(),
                testTerms.getVersion(),
                EffectiveDate.of(newDate),
                testTerms.getRequired(),
                testTerms.getCreatedAt(),
                LocalDateTime.now() // 업데이트 시간은 현재 시간으로
        );

        given(termsService.updateTermsEffectiveDate(anyLong(), any(LocalDate.class))).willReturn(updatedTerms);

        // When
        ResponseEntity<Terms> response = termsAdminController.updateTermsEffectiveDate(1L, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getEffectiveDate().getValue()).isEqualTo(newDate);
        verify(termsService).updateTermsEffectiveDate(1L, newDate);
    }

    @Test
    @DisplayName("약관 필수 여부 업데이트 성공 테스트")
    void updateTermsRequired_ShouldReturnUpdatedTerms() {
        // Given
        UpdateTermsRequiredRequest request = new UpdateTermsRequiredRequest(Required.of(false));

        // 필수 여부가 업데이트된 약관 객체 생성
        Terms updatedTerms = Terms.of(
                testTerms.getId(),
                testTerms.getTitle(),
                testTerms.getContent(),
                testTerms.getType(),
                testTerms.getVersion(),
                testTerms.getEffectiveDate(),
                Required.of(false),
                testTerms.getCreatedAt(),
                LocalDateTime.now() // 업데이트 시간은 현재 시간으로
        );

        given(termsService.updateTermsRequired(anyLong(), anyBoolean())).willReturn(updatedTerms);

        // When
        ResponseEntity<Terms> response = termsAdminController.updateTermsRequired(1L, request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isRequiredValue()).isFalse();
        verify(termsService).updateTermsRequired(1L, false);
    }

    @Test
    @DisplayName("약관 삭제 성공 테스트")
    void deleteTerms_ShouldReturnNoContent() {
        // Given
        doNothing().when(termsService).deleteTerms(anyLong());

        // When
        ResponseEntity<Void> response = termsAdminController.deleteTerms(1L);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(termsService).deleteTerms(1L);
    }
}