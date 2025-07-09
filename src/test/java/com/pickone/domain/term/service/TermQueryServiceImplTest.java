package com.pickone.domain.term.service;

import com.pickone.domain.term.dto.TermResponse;
import com.pickone.domain.term.entity.Term;
import com.pickone.domain.term.mapper.TermMapper;
import com.pickone.domain.term.repository.TermJpaRepository;
import com.pickone.domain.term.repository.TermQueryRepository;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TermQueryServiceImplTest {

  @InjectMocks
  private TermQueryServiceImpl termQueryService;

  @Mock
  private TermJpaRepository termRepository;

  @Mock
  private TermQueryRepository termQueryRepository;

  @Mock
  private TermMapper termMapper;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void getTerm_success() {
    // given
    Long termId = 1L;
    Term term = mock(Term.class);
    TermResponse response = new TermResponse(termId, "약관", "내용", "v1", true, LocalDateTime.now());

    when(termRepository.findById(termId)).thenReturn(Optional.of(term));
    when(termMapper.toDto(term)).thenReturn(response);

    // when
    TermResponse result = termQueryService.getTerm(termId);

    // then
    assertEquals(termId, result.id());
    verify(termRepository).findById(termId);
    verify(termMapper).toDto(term);
    verifyNoMoreInteractions(termRepository, termMapper);
  }

  @Test
  void getTerm_fail_termNotFound() {
    // given
    Long termId = 999L;
    when(termRepository.findById(termId)).thenReturn(Optional.empty());

    // when & then
    BusinessException ex = assertThrows(BusinessException.class, () -> termQueryService.getTerm(termId));
    assertEquals(ErrorCode.TERM_NOT_FOUND, ex.getErrorCode());

    verify(termRepository).findById(termId);
    verifyNoMoreInteractions(termRepository);
  }

  @Test
  void getLatestTerms_success() {
    // given
    Term term1 = mock(Term.class);
    Term term2 = mock(Term.class);
    List<Term> terms = List.of(term1, term2);

    TermResponse response1 = new TermResponse(1L, "약관1", "내용1", "v1", true, LocalDateTime.now());
    TermResponse response2 = new TermResponse(2L, "약관2", "내용2", "v1", false, LocalDateTime.now());

    when(termQueryRepository.findLatestTerms()).thenReturn(terms);
    when(termMapper.toDto(term1)).thenReturn(response1);
    when(termMapper.toDto(term2)).thenReturn(response2);

    // when
    List<TermResponse> results = termQueryService.getLatestTerms();

    // then
    assertEquals(2, results.size());
    verify(termQueryRepository).findLatestTerms();
    verify(termMapper, times(2)).toDto(any());
    verifyNoMoreInteractions(termQueryRepository, termMapper);
  }

  @Test
  void getRequiredLatestTermIds_success() {
    // given
    List<Long> ids = List.of(1L, 2L, 3L);
    when(termQueryRepository.findRequiredLatestTermIds()).thenReturn(ids);

    // when
    List<Long> result = termQueryService.getRequiredLatestTermIds();

    // then
    assertEquals(3, result.size());
    verify(termQueryRepository).findRequiredLatestTermIds();
    verifyNoMoreInteractions(termQueryRepository);
  }
}
