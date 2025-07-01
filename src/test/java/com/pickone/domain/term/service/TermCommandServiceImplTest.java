package com.pickone.domain.term.service;

import com.pickone.domain.term.dto.TermRequestDto;
import com.pickone.domain.term.dto.TermResponseDto;
import com.pickone.domain.term.model.entity.TermEntity;
import com.pickone.domain.term.model.factory.TermFactory;
import com.pickone.domain.term.model.policy.TermPolicy;
import com.pickone.domain.term.model.mapper.TermMapper;
import com.pickone.domain.term.repository.TermJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class TermCommandServiceImplTest {

  @Mock private TermJpaRepository termRepository;
  @Mock private TermFactory termFactory;
  @Mock private TermPolicy termPolicy;
  @InjectMocks private TermCommandServiceImpl sut;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Nested
  @DisplayName("createTerm")
  class CreateTerm {
    @Test
    @DisplayName("정상적으로 약관 생성")
    void createTerm_success() {
      TermRequestDto dto = mock(TermRequestDto.class);

      doNothing().when(termPolicy).validateCreate(dto);

      TermEntity entity = mock(TermEntity.class);
      TermEntity saved = mock(TermEntity.class);

      when(termFactory.create(dto)).thenReturn(entity);
      when(termRepository.save(entity)).thenReturn(saved);

      TermResponseDto responseDto = mock(TermResponseDto.class);

      try (MockedStatic<TermMapper> staticMapper = mockStatic(TermMapper.class)) {
        staticMapper.when(() -> TermMapper.toDto(saved)).thenReturn(responseDto);

        TermResponseDto result = sut.createTerm(dto);

        assertThat(result).isSameAs(responseDto);
        verify(termPolicy).validateCreate(dto);
        verify(termFactory).create(dto);
        verify(termRepository).save(entity);
      }
    }
  }

  @Nested
  @DisplayName("updateTerm")
  class UpdateTerm {
    @Test
    @DisplayName("불변 엔티티 업데이트 시 UnsupportedOperationException 발생")
    void updateTerm_unsupported() {
      Long termId = 123L;
      TermRequestDto dto = mock(TermRequestDto.class);

      TermEntity entity = mock(TermEntity.class);
      when(termRepository.findById(termId)).thenReturn(Optional.of(entity));

      assertThatThrownBy(() -> sut.updateTerm(termId, dto))
          .isInstanceOf(UnsupportedOperationException.class)
          .hasMessageContaining("불변 엔티티");
    }

    @Test
    @DisplayName("존재하지 않는 약관 업데이트 시 예외 발생")
    void updateTerm_notFound() {
      Long termId = 99L;
      TermRequestDto dto = mock(TermRequestDto.class);

      when(termRepository.findById(termId)).thenReturn(Optional.empty());

      assertThatThrownBy(() -> sut.updateTerm(termId, dto))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Term not found");
    }
  }

  @Nested
  @DisplayName("deleteTerm")
  class DeleteTerm {
    @Test
    @DisplayName("정상적으로 약관 삭제")
    void deleteTerm_success() {
      Long termId = 88L;

      doNothing().when(termRepository).deleteById(termId);

      sut.deleteTerm(termId);

      verify(termRepository).deleteById(termId);
    }
  }
}
