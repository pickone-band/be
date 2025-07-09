package com.pickone.domain.term.service;

import com.pickone.domain.term.dto.TermRequest;
import com.pickone.domain.term.dto.TermResponse;
import com.pickone.domain.term.entity.Term;
import com.pickone.domain.term.mapper.TermMapper;
import com.pickone.domain.term.repository.TermJpaRepository;
import com.pickone.domain.term.validator.TermValidator;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TermCommandServiceImplTest {

  @InjectMocks
  private TermCommandServiceImpl termCommandService;

  @Mock
  private TermJpaRepository termRepository;

  @Mock
  private TermMapper termMapper;

  @Mock
  private TermValidator validator;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void createTerm_success() {
    // given
    TermRequest request = new TermRequest("약관1", "내용1", "v1.0", true, LocalDateTime.now());

    Term entity = mock(Term.class);
    Term saved = mock(Term.class);

    when(termMapper.toEntity(request)).thenReturn(entity);
    when(termRepository.save(entity)).thenReturn(saved);
    when(saved.getId()).thenReturn(1L);
    when(saved.getTitle()).thenReturn(request.title());
    when(saved.getContent()).thenReturn(request.content());
    when(saved.getVersion()).thenReturn(request.version());
    when(saved.isRequired()).thenReturn(request.required());
    when(saved.getEffectiveDate()).thenReturn(request.effectiveDate());

    TermResponse response = new TermResponse(
        1L, request.title(), request.content(), request.version(), request.required(), request.effectiveDate()
    );
    when(termMapper.toDto(saved)).thenReturn(response);

    // when
    TermResponse result = termCommandService.createTerm(request);

    // then
    assertEquals(1L, result.id());
    assertEquals("약관1", result.title());
    verify(termMapper).toEntity(request);
    verify(termRepository).save(entity);
    verify(termMapper).toDto(saved);
    verify(validator).validateCreate(request);
    verifyNoMoreInteractions(termMapper, termRepository, validator);
  }

  @Test
  void updateTerm_success() {
    // given
    Long termId = 1L;
    TermRequest request = new TermRequest("수정된 제목", "수정된 내용", "v1", false, LocalDateTime.now());

    Term existing = mock(Term.class);
    Term updated = mock(Term.class);

    when(termRepository.findById(termId)).thenReturn(Optional.of(existing));
    when(existing.withUpdate(any(), any(), anyBoolean(), any())).thenReturn(updated);
    when(termRepository.save(updated)).thenReturn(updated);

    // when
    termCommandService.updateTerm(termId, request);

    // then
    verify(termRepository).findById(termId);
    verify(validator).validateUpdate(existing, request);
    verify(existing).withUpdate(request.title(), request.content(), request.required(), request.effectiveDate());
    verify(termRepository).save(updated);
    verifyNoMoreInteractions(termRepository, validator);
  }

  @Test
  void updateTerm_fail_termNotFound() {
    // given
    Long termId = 999L;
    TermRequest request = new TermRequest("제목", "내용", "v1", true, LocalDateTime.now());

    when(termRepository.findById(termId)).thenReturn(Optional.empty());

    // when & then
    BusinessException ex = assertThrows(
        BusinessException.class,
        () -> termCommandService.updateTerm(termId, request)
    );

    assertEquals(ErrorCode.TERM_NOT_FOUND, ex.getErrorCode());
    verify(termRepository).findById(termId);
    verifyNoMoreInteractions(termRepository, validator);
  }

  @Test
  void deleteTerm_success() {
    // given
    Long id = 1L;
    when(termRepository.existsById(id)).thenReturn(true);

    // when
    termCommandService.deleteTerm(id);

    // then
    verify(termRepository).existsById(id);
    verify(termRepository).deleteById(id);
    verifyNoMoreInteractions(termRepository);
  }

  @Test
  void deleteTerm_fail_termNotFound() {
    // given
    Long id = 999L;
    when(termRepository.existsById(id)).thenReturn(false);

    // when & then
    BusinessException ex = assertThrows(
        BusinessException.class,
        () -> termCommandService.deleteTerm(id)
    );

    assertEquals(ErrorCode.TERM_NOT_FOUND, ex.getErrorCode());
    verify(termRepository).existsById(id);
    verify(termRepository, never()).deleteById(any());
    verifyNoMoreInteractions(termRepository);
  }
}
