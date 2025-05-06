package com.PickOne.domain.term.service;

import com.PickOne.domain.term.model.domain.Term;
import com.PickOne.domain.term.repository.TermRepository;
import com.PickOne.domain.term.service.TermServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * TermServiceImpl 클래스에 대한 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class TermServiceImplTest {

    @Mock
    private TermRepository termRepository;

    @InjectMocks
    private TermServiceImpl termService;

    private final Long termId = 1L;
    private final LocalDateTime now = LocalDateTime.now();

    /**
     * 약관 생성 테스트
     */
    @Test
    @DisplayName("약관 생성 테스트 - 성공 케이스")
    void createTerms_Success() {
        // given
        Term term = mock(Term.class);
        when(termRepository.save(term)).thenReturn(term);

        // when
        Term createdTerm = termService.createTerms(term);

        // then
        verify(termRepository).save(term);
        assertThat(createdTerm).isEqualTo(term);
    }

    /**
     * ID로 약관 조회 테스트 - 성공 케이스
     */
    @Test
    @DisplayName("ID로 약관 조회 테스트 - 성공 케이스")
    void getTermsById_Success() {
        // given
        Term term = mock(Term.class);
        when(termRepository.findById(termId)).thenReturn(Optional.of(term));

        // when
        Term foundTerm = termService.getTermsById(termId);

        // then
        verify(termRepository).findById(termId);
        assertThat(foundTerm).isEqualTo(term);
    }

    /**
     * ID로 약관 조회 테스트 - 실패 케이스(약관이 존재하지 않음)
     */
    @Test
    @DisplayName("ID로 약관 조회 테스트 - 실패 케이스(약관이 존재하지 않음)")
    void getTermsById_NotFound() {
        // given
        when(termRepository.findById(termId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> termService.getTermsById(termId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("약관을 찾을 수 없습니다");
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

        when(termRepository.findRequired(any(LocalDateTime.class))).thenReturn(requiredTerms);

        // when
        List<Term> foundTerms = termService.getRequiredTerms();

        // then
        verify(termRepository).findRequired(any(LocalDateTime.class));
        assertThat(foundTerms).hasSize(2);
        assertThat(foundTerms).containsExactly(term1, term2);
    }

    /**
     * 약관 삭제 테스트
     */
    @Test
    @DisplayName("약관 삭제 테스트 - 성공 케이스")
    void deleteById_Success() {
        // given
        doNothing().when(termRepository).deleteById(termId);

        // when
        termService.deleteById(termId);

        // then
        verify(termRepository).deleteById(termId);
    }
}