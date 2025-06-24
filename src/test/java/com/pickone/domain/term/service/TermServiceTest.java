package com.pickone.domain.term.service;

import com.pickone.domain.term.model.entity.TermEntity;
import com.pickone.domain.term.repository.TermJpaRepository;
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

    private final TermEntity mockTerm = new TermEntity(
            1L,
            "제목",
            "내용",
            "v1.0",
            true,
            LocalDateTime.now()
    );

    @Test
    @DisplayName("약관 등록에 성공한다")
    void createTerm_success() {
        when(termJpaRepository.save(any(TermEntity.class))).thenReturn(mockTerm);

        TermEntity result = termService.create(mockTerm);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("제목");
        verify(termJpaRepository).save(mockTerm);
    }

    @Test
    @DisplayName("약관 단건 조회에 성공한다")
    void getTermById_success() {
        when(termJpaRepository.findById(1L)).thenReturn(Optional.of(mockTerm));

        TermEntity result = termService.getById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getVersion()).isEqualTo("v1.0");
        verify(termJpaRepository).findById(1L);
    }

    @Test
    @DisplayName("약관 전체 조회에 성공한다")
    void getAllTerms_success() {
        when(termJpaRepository.findAll()).thenReturn(List.of(mockTerm));

        List<TermEntity> terms = termService.getAll();

        assertThat(terms).hasSize(1);
        verify(termJpaRepository).findAll();
    }

    @Test
    @DisplayName("약관 삭제에 성공한다")
    void deleteTerm_success() {
        termService.delete(1L);
        verify(termJpaRepository).deleteById(1L);
    }
}
