package com.PickOne.domain.term.service;

import com.PickOne.domain.term.model.domain.Term;
import com.PickOne.domain.term.repository.TermRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class TermServiceTest {

    @Mock private TermRepository termRepository;
    @InjectMocks private TermService termService;

    private final Term mockTerm = new Term(
            1L,
            "제목",
            "내용",
            "v1.0",
            true,
            LocalDateTime.now()
    );

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 약관_등록_성공() {
        when(termRepository.save(any(Term.class))).thenReturn(mockTerm);
        Term result = termService.create(mockTerm);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("제목");
        verify(termRepository).save(mockTerm);
    }

    @Test
    void 약관_단건_조회_성공() {
        when(termRepository.findById(1L)).thenReturn(Optional.of(mockTerm));

        Term result = termService.getById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getVersion()).isEqualTo("v1.0");
        verify(termRepository).findById(1L);
    }

    @Test
    void 약관_전체_조회_성공() {
        when(termRepository.findAll()).thenReturn(List.of(mockTerm));
        List<Term> terms = termService.getAll();

        assertThat(terms).hasSize(1);
        verify(termRepository).findAll();
    }

    @Test
    void 약관_삭제_성공() {
        termService.delete(1L);
        verify(termRepository).deleteById(1L);
    }
}
