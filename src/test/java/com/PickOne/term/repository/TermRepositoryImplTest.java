package com.PickOne.term.repository;

import com.PickOne.term.model.domain.Term;
import com.PickOne.term.model.entity.TermsEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * TermRepositoryImpl 클래스에 대한 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class TermRepositoryImplTest {

    @Mock
    private JpaTermRepository jpaRepository;

    @InjectMocks
    private TermRepositoryImpl termRepository;

    private final Long termId = 1L;
    private final LocalDateTime now = LocalDateTime.now();

    /**
     * 약관 저장 테스트
     */
    @Test
    @DisplayName("약관 저장 테스트 - 성공 케이스")
    void save_Success() {
        // given
        Term term = mock(Term.class);
        TermsEntity savedEntity = mock(TermsEntity.class);
        Term expectedTerm = mock(Term.class);

        // TermsEntity.from은 정적 메서드로 모킹이 어려우므로 JpaRepository의 save 메서드만 모킹
        when(jpaRepository.save(any(TermsEntity.class))).thenReturn(savedEntity);
        when(savedEntity.toDomain()).thenReturn(expectedTerm);

        // when
        Term result = termRepository.save(term);

        // then
        verify(jpaRepository).save(any(TermsEntity.class));
        assertThat(result).isEqualTo(expectedTerm);
    }

    /**
     * ID로 약관 조회 테스트 - 성공 케이스
     */
    @Test
    @DisplayName("ID로 약관 조회 테스트 - 성공 케이스")
    void findById_Success() {
        // given
        TermsEntity entity = mock(TermsEntity.class);
        Term expectedTerm = mock(Term.class);

        when(jpaRepository.findById(termId)).thenReturn(Optional.of(entity));
        when(entity.toDomain()).thenReturn(expectedTerm);

        // when
        Optional<Term> result = termRepository.findById(termId);

        // then
        verify(jpaRepository).findById(termId);
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(expectedTerm);
    }

    /**
     * ID로 약관 조회 테스트 - 실패 케이스 (약관이 존재하지 않음)
     */
    @Test
    @DisplayName("ID로 약관 조회 테스트 - 실패 케이스 (약관이 존재하지 않음)")
    void findById_NotFound() {
        // given
        when(jpaRepository.findById(termId)).thenReturn(Optional.empty());

        // when
        Optional<Term> result = termRepository.findById(termId);

        // then
        verify(jpaRepository).findById(termId);
        assertThat(result).isEmpty();
    }

    /**
     * 약관 삭제 테스트
     */
    @Test
    @DisplayName("약관 삭제 테스트 - 성공 케이스")
    void deleteById_Success() {
        // given
        doNothing().when(jpaRepository).deleteById(termId);

        // when
        termRepository.deleteById(termId);

        // then
        verify(jpaRepository).deleteById(termId);
    }

    /**
     * 필수 약관 목록 조회 테스트
     */
    @Test
    @DisplayName("필수 약관 목록 조회 테스트 - 성공 케이스")
    void findRequired_Success() {
        // given
        TermsEntity entity1 = mock(TermsEntity.class);
        TermsEntity entity2 = mock(TermsEntity.class);
        Term term1 = mock(Term.class);
        Term term2 = mock(Term.class);

        when(entity1.toDomain()).thenReturn(term1);
        when(entity2.toDomain()).thenReturn(term2);

        List<TermsEntity> entities = Arrays.asList(entity1, entity2);
        when(jpaRepository.findRequired(now)).thenReturn(entities);

        // when
        List<Term> results = termRepository.findRequired(now);

        // then
        verify(jpaRepository).findRequired(now);
        assertThat(results).hasSize(2);
        assertThat(results).containsExactly(term1, term2);
    }

    /**
     * 필수 약관 목록 조회 테스트 - 결과 없음
     */
    @Test
    @DisplayName("필수 약관 목록 조회 테스트 - 결과 없음")
    void findRequired_NoResults() {
        // given
        when(jpaRepository.findRequired(now)).thenReturn(List.of());

        // when
        List<Term> results = termRepository.findRequired(now);

        // then
        verify(jpaRepository).findRequired(now);
        assertThat(results).isEmpty();
    }
}