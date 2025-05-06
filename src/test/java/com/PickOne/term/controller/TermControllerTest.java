package com.PickOne.term.controller;

import com.PickOne.domain.term.controller.TermController;
import com.PickOne.domain.term.model.domain.*;
import com.PickOne.global.exception.BaseResponse;
import com.PickOne.global.exception.SuccessCode;
import com.PickOne.domain.term.dto.TermsRequest;
import com.PickOne.domain.term.service.TermService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * TermController 클래스에 대한 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class TermControllerTest {

    @Mock
    private TermService termService;

    @InjectMocks
    private TermController termController;

    private final Long termId = 1L;
    private final LocalDateTime now = LocalDateTime.now();

    /**
     * 약관 생성 테스트
     */
    @Test
    @DisplayName("약관 생성 테스트 - 성공 케이스")
    void createTerms_Success() {
        // given
        Title title = mock(Title.class);
        Content content = mock(Content.class);
        TermsType type = mock(TermsType.class);
        Version version = mock(Version.class);
        EffectiveDate effectiveDate = mock(EffectiveDate.class);
        Required required = mock(Required.class);

        TermsRequest request = new TermsRequest(
                title, content, type, version, required, effectiveDate
        );

        Term term = mock(Term.class);
        when(termService.createTerms(any(Term.class))).thenReturn(term);

        // when
        ResponseEntity<BaseResponse<Term>> response = termController.createTerms(request);

        // then
        verify(termService).createTerms(any(Term.class));
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getResult()).isEqualTo(term);
        assertThat(response.getBody().getIsSuccess()).isTrue();
        assertThat(response.getBody().getMessage()).isEqualTo(SuccessCode.CREATED.getMessage());
    }

    /**
     * ID로 약관 조회 테스트
     */
    @Test
    @DisplayName("ID로 약관 조회 테스트 - 성공 케이스")
    void getTermsById_Success() {
        // given
        Term term = mock(Term.class);
        when(termService.getTermsById(termId)).thenReturn(term);

        // when
        ResponseEntity<BaseResponse<Term>> response = termController.getTermsById(termId);

        // then
        verify(termService).getTermsById(termId);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getResult()).isEqualTo(term);
        assertThat(response.getBody().getIsSuccess()).isTrue();
    }

    /**
     * 필수 약관 목록 조회 테스트
     */
    @Test
    @DisplayName("필수 약관 목록 조회 테스트 - 성공 케이스")
    void getRequiredTerms_Success() {
        // given
        Term term1 = mock(Term.class);
        Term term2 = mock(Term.class);
        List<Term> requiredTerms = Arrays.asList(term1, term2);

        when(termService.getRequiredTerms()).thenReturn(requiredTerms);

        // when
        ResponseEntity<BaseResponse<List<Term>>> response = termController.getRequiredTerms();

        // then
        verify(termService).getRequiredTerms();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getResult()).hasSize(2);
        assertThat(response.getBody().getResult()).containsExactly(term1, term2);
        assertThat(response.getBody().getIsSuccess()).isTrue();
    }

    /**
     * 약관 삭제 테스트
     */
    @Test
    @DisplayName("약관 삭제 테스트 - 성공 케이스")
    void deleteTerms_Success() {
        // given
        doNothing().when(termService).deleteById(termId);

        // when
        ResponseEntity<BaseResponse<Void>> response = termController.deleteTerms(termId);

        // then
        verify(termService).deleteById(termId);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getResult()).isNull();
        assertThat(response.getBody().getIsSuccess()).isTrue();
        assertThat(response.getBody().getMessage()).isEqualTo(SuccessCode.DELETED.getMessage());
    }
}