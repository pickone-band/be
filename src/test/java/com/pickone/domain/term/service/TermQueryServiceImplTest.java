package com.pickone.domain.term.service;

import com.pickone.domain.term.dto.TermResponseDto;
import com.pickone.domain.term.model.entity.TermEntity;
import com.pickone.domain.term.model.mapper.TermMapper;
import com.pickone.domain.term.repository.TermJpaRepository;
import com.pickone.domain.term.repository.TermQueryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class TermQueryServiceImplTest {

  @Mock private TermJpaRepository termRepository;
  @Mock private TermQueryRepository termQueryRepository;
  @InjectMocks private TermQueryServiceImpl sut;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Nested
  @DisplayName("getTerm")
  class GetTerm {
    @Test
    @DisplayName("존재하는 약관을 반환")
    void getTerm_success() {
      Long termId = 1L;
      TermEntity entity = mock(TermEntity.class);
      TermResponseDto dto = mock(TermResponseDto.class);

      when(termRepository.findById(termId)).thenReturn(Optional.of(entity));

      try (MockedStatic<TermMapper> staticMapper = mockStatic(TermMapper.class)) {
        staticMapper.when(() -> TermMapper.toDto(entity)).thenReturn(dto);

        TermResponseDto result = sut.getTerm(termId);

        assertThat(result).isSameAs(dto);
        verify(termRepository).findById(termId);
      }
    }

    @Test
    @DisplayName("약관이 없으면 예외 발생")
    void getTerm_notFound() {
      Long termId = 99L;
      when(termRepository.findById(termId)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> sut.getTerm(termId))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Term not found");
    }
  }

  @Nested
  @DisplayName("getLatestTerms")
  class GetLatestTerms {
    @Test
    @DisplayName("최신 약관 리스트 반환")
    void getLatestTerms_success() {
      TermEntity e1 = mock(TermEntity.class);
      TermEntity e2 = mock(TermEntity.class);
      List<TermEntity> entities = Arrays.asList(e1, e2);
      when(termQueryRepository.findLatestTerms()).thenReturn(entities);

      TermResponseDto d1 = mock(TermResponseDto.class);
      TermResponseDto d2 = mock(TermResponseDto.class);

      try (MockedStatic<TermMapper> staticMapper = mockStatic(TermMapper.class)) {
        staticMapper.when(() -> TermMapper.toDto(e1)).thenReturn(d1);
        staticMapper.when(() -> TermMapper.toDto(e2)).thenReturn(d2);

        List<TermResponseDto> result = sut.getLatestTerms();

        assertThat(result).containsExactly(d1, d2);
        verify(termQueryRepository).findLatestTerms();
      }
    }

    @Test
    @DisplayName("최신 약관이 없으면 빈 리스트")
    void getLatestTerms_empty() {
      when(termQueryRepository.findLatestTerms()).thenReturn(Collections.emptyList());

      List<TermResponseDto> result = sut.getLatestTerms();

      assertThat(result).isEmpty();
      verify(termQueryRepository).findLatestTerms();
    }
  }
}
