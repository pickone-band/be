package com.pickone.domain.term.service;

import com.pickone.domain.term.model.entity.TermEntity;
import com.pickone.domain.term.repository.TermJpaRepository;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TermService 단위 테스트")
class TermServiceTest {

  @Mock private TermJpaRepository termJpaRepository;
  @InjectMocks private TermService termService;

  // 테스트에서 사용할 TermEntity 생성 (정적 팩토리 메서드 사용)
  private final TermEntity mockTerm = TermEntity.create(
      "제목",
      "내용",
      "v1.0",
      true,
      LocalDateTime.of(2024, 6, 30, 12, 0)
  );

  @Test
  @DisplayName("약관 등록에 성공한다")
  void createTerm_success() {
    when(termJpaRepository.save(any(TermEntity.class))).thenReturn(mockTerm);

    TermEntity result = termService.create(mockTerm);

    assertThat(result).isNotNull();
    assertThat(result.getTitle()).isEqualTo("제목");
    assertThat(result.getVersion()).isEqualTo("v1.0");
    verify(termJpaRepository).save(mockTerm);
  }

  @Test
  @DisplayName("약관 단건 조회에 성공한다")
  void getTermById_success() {
    when(termJpaRepository.findById(any(Long.class))).thenReturn(Optional.of(mockTerm));

    TermEntity result = termService.getById(1L);

    assertThat(result.getTitle()).isEqualTo("제목");
    assertThat(result.getId()).isNull(); // mockTerm의 id는 null (DB 저장 전이므로)
    verify(termJpaRepository).findById(1L);
  }

  @Test
  @DisplayName("약관 단건 조회 실패 - 예외 발생")
  void getTermById_notFound_throwsException() {
    when(termJpaRepository.findById(any(Long.class))).thenReturn(Optional.empty());

    assertThatThrownBy(() -> termService.getById(999L))
        .isInstanceOf(BusinessException.class);


    verify(termJpaRepository).findById(999L);
  }

  @Test
  @DisplayName("약관 전체 조회에 성공한다")
  void getAllTerms_success() {
    when(termJpaRepository.findAll()).thenReturn(List.of(mockTerm));

    List<TermEntity> terms = termService.getAll();

    assertThat(terms).hasSize(1);
    assertThat(terms.get(0).getTitle()).isEqualTo("제목");
    verify(termJpaRepository).findAll();
  }

  @Test
  @DisplayName("필수 약관만 조회에 성공한다")
  void getRequiredTerms_success() {
    when(termJpaRepository.findByRequiredTrue()).thenReturn(List.of(mockTerm));

    List<TermEntity> terms = termService.getRequiredTerms();

    assertThat(terms).hasSize(1);
    assertThat(terms.get(0).isRequired()).isTrue();
    verify(termJpaRepository).findByRequiredTrue();
  }

  @Test
  @DisplayName("약관 삭제에 성공한다")
  void deleteTerm_success() {
    // 실제 deleteById는 void 반환, 따로 when/thenReturn 필요 없음
    termService.delete(1L);
    verify(termJpaRepository).deleteById(1L);
  }
}