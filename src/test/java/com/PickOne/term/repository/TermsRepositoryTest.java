package com.PickOne.term.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.PickOne.term.model.domain.Terms;
import com.PickOne.term.model.domain.TermsType;
import com.PickOne.term.model.entity.TermsEntity;
import com.PickOne.term.repository.JpaTermsRepository;
import com.PickOne.term.repository.TermsRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TermsRepositoryTest {

    @Mock
    private JpaTermsRepository jpaTermsRepository;

    @InjectMocks
    private TermsRepositoryImpl termsRepository;

    private final LocalDate TODAY = LocalDate.now();

    @Test
    @DisplayName("약관 저장 테스트")
    void save_ShouldReturnSavedTerms() {
        // given
        TermsEntity termsEntity = mock(TermsEntity.class);
        Terms terms = mock(Terms.class);
        when(termsEntity.toDomain()).thenReturn(terms);
        when(jpaTermsRepository.save(any(TermsEntity.class))).thenReturn(termsEntity);

        // when
        Terms savedTerms = termsRepository.save(terms);

        // then
        assertThat(savedTerms).isEqualTo(terms);
        verify(jpaTermsRepository).save(any(TermsEntity.class));
    }

    @Test
    @DisplayName("ID로 약관 조회 테스트")
    void findById_ShouldReturnCorrectTerms() {
        // given
        TermsEntity termsEntity = mock(TermsEntity.class);
        Terms terms = mock(Terms.class);
        when(termsEntity.toDomain()).thenReturn(terms);
        when(jpaTermsRepository.findById(1L)).thenReturn(Optional.of(termsEntity));

        // when
        Optional<Terms> foundTerms = termsRepository.findById(1L);

        // then
        assertThat(foundTerms).isPresent();
        assertThat(foundTerms.get()).isEqualTo(terms);
        verify(jpaTermsRepository).findById(1L);
    }

    @Test
    @DisplayName("유형별 최신 약관 조회 테스트")
    void findLatestByType_ShouldReturnMostRecentVersion() {
        // given
        TermsEntity termsEntity = mock(TermsEntity.class);
        Terms terms = mock(Terms.class);
        when(termsEntity.toDomain()).thenReturn(terms);
        when(jpaTermsRepository.findLatestByType(TermsType.SERVICE)).thenReturn(Optional.of(termsEntity));

        // when
        Optional<Terms> latestTerms = termsRepository.findLatestByType(TermsType.SERVICE);

        // then
        assertThat(latestTerms).isPresent();
        assertThat(latestTerms.get()).isEqualTo(terms);
        verify(jpaTermsRepository).findLatestByType(TermsType.SERVICE);
    }

    @Test
    @DisplayName("현재 유효한 약관 조회 테스트")
    void findCurrentlyEffectiveByType_ShouldReturnCurrentValidTerms() {
        // given
        TermsEntity termsEntity = mock(TermsEntity.class);
        Terms terms = mock(Terms.class);
        when(termsEntity.toDomain()).thenReturn(terms);
        when(jpaTermsRepository.findCurrentlyEffectiveByType(TermsType.SERVICE, TODAY))
                .thenReturn(Optional.of(termsEntity));

        // when
        Optional<Terms> effectiveTerms = termsRepository.findCurrentlyEffectiveByType(TermsType.SERVICE, TODAY);

        // then
        assertThat(effectiveTerms).isPresent();
        assertThat(effectiveTerms.get()).isEqualTo(terms);
        verify(jpaTermsRepository).findCurrentlyEffectiveByType(TermsType.SERVICE, TODAY);
    }

    @Test
    @DisplayName("필수 동의 약관 전체 조회 테스트")
    void findAllRequiredAndEffective_ShouldReturnRequiredTerms() {
        // given
        TermsEntity termsEntity1 = mock(TermsEntity.class);
        TermsEntity termsEntity2 = mock(TermsEntity.class);
        Terms terms1 = mock(Terms.class);
        Terms terms2 = mock(Terms.class);

        when(termsEntity1.toDomain()).thenReturn(terms1);
        when(termsEntity2.toDomain()).thenReturn(terms2);

        List<TermsEntity> entities = Arrays.asList(termsEntity1, termsEntity2);
        when(jpaTermsRepository.findAllRequiredAndEffective(TODAY)).thenReturn(entities);

        // when
        List<Terms> requiredTerms = termsRepository.findAllRequiredAndEffective(TODAY);

        // then
        assertThat(requiredTerms).hasSize(2);
        assertThat(requiredTerms).containsExactly(terms1, terms2);
        verify(jpaTermsRepository).findAllRequiredAndEffective(TODAY);
    }

    @Test
    @DisplayName("약관 삭제 테스트")
    void deleteById_ShouldCallRepositoryMethod() {
        // given
        doNothing().when(jpaTermsRepository).deleteById(1L);

        // when
        termsRepository.deleteById(1L);

        // then
        verify(jpaTermsRepository).deleteById(1L);
    }
}