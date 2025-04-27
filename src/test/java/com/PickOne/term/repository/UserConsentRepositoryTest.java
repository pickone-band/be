package com.PickOne.term.repository;

import com.PickOne.term.model.domain.UserConsent;
import com.PickOne.term.model.entity.UserConsentEntity;
import com.PickOne.term.repository.userConsent.JpaUserConsentRepository;
import com.PickOne.term.repository.userConsent.UserConsentRepositoryImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserConsentRepositoryTest {

    @Mock
    private JpaUserConsentRepository jpaUserConsentRepository;

    @InjectMocks
    private UserConsentRepositoryImpl userConsentRepository;

    private final Long USER_ID = 1L;
    private final Long TERMS_ID = 2L;

    @Test
    @DisplayName("사용자 동의 저장 테스트")
    void save_ShouldReturnSavedConsent() {
        // given
        UserConsentEntity userConsentEntity = mock(UserConsentEntity.class);
        UserConsent userConsent = mock(UserConsent.class);
        when(userConsentEntity.toDomain()).thenReturn(userConsent);
        when(jpaUserConsentRepository.save(any(UserConsentEntity.class))).thenReturn(userConsentEntity);

        // when
        UserConsent savedConsent = userConsentRepository.save(userConsent);

        // then
        assertThat(savedConsent).isEqualTo(userConsent);
        verify(jpaUserConsentRepository).save(any(UserConsentEntity.class));
    }

    @Test
    @DisplayName("사용자 ID와 약관 ID로 동의 정보 조회 테스트")
    void findByUserIdAndTermsId_ShouldReturnCorrectConsent() {
        // given
        UserConsentEntity userConsentEntity = mock(UserConsentEntity.class);
        UserConsent userConsent = mock(UserConsent.class);
        when(userConsentEntity.toDomain()).thenReturn(userConsent);
        when(jpaUserConsentRepository.findByUserIdAndTermsId(USER_ID, TERMS_ID))
                .thenReturn(Optional.of(userConsentEntity));

        // when
        Optional<UserConsent> foundConsent = userConsentRepository.findByUserIdAndTermsId(USER_ID, TERMS_ID);

        // then
        assertThat(foundConsent).isPresent();
        assertThat(foundConsent.get()).isEqualTo(userConsent);
        verify(jpaUserConsentRepository).findByUserIdAndTermsId(USER_ID, TERMS_ID);
    }

    @Test
    @DisplayName("사용자 ID로 모든 동의 정보 조회 테스트")
    void findAllByUserId_ShouldReturnAllUserConsents() {
        // given
        UserConsentEntity userConsentEntity1 = mock(UserConsentEntity.class);
        UserConsentEntity userConsentEntity2 = mock(UserConsentEntity.class);
        UserConsent userConsent1 = mock(UserConsent.class);
        UserConsent userConsent2 = mock(UserConsent.class);

        when(userConsentEntity1.toDomain()).thenReturn(userConsent1);
        when(userConsentEntity2.toDomain()).thenReturn(userConsent2);

        List<UserConsentEntity> entities = Arrays.asList(userConsentEntity1, userConsentEntity2);
        when(jpaUserConsentRepository.findAllByUserId(USER_ID)).thenReturn(entities);

        // when
        List<UserConsent> consents = userConsentRepository.findAllByUserId(USER_ID);

        // then
        assertThat(consents).hasSize(2);
        assertThat(consents).containsExactly(userConsent1, userConsent2);
        verify(jpaUserConsentRepository).findAllByUserId(USER_ID);
    }

    @Test
    @DisplayName("약관 ID로 모든 동의 정보 조회 테스트")
    void findAllByTermsId_ShouldReturnAllTermsConsents() {
        // given
        UserConsentEntity userConsentEntity1 = mock(UserConsentEntity.class);
        UserConsentEntity userConsentEntity2 = mock(UserConsentEntity.class);
        UserConsent userConsent1 = mock(UserConsent.class);
        UserConsent userConsent2 = mock(UserConsent.class);

        when(userConsentEntity1.toDomain()).thenReturn(userConsent1);
        when(userConsentEntity2.toDomain()).thenReturn(userConsent2);

        List<UserConsentEntity> entities = Arrays.asList(userConsentEntity1, userConsentEntity2);
        when(jpaUserConsentRepository.findAllByTermsId(TERMS_ID)).thenReturn(entities);

        // when
        List<UserConsent> consents = userConsentRepository.findAllByTermsId(TERMS_ID);

        // then
        assertThat(consents).hasSize(2);
        assertThat(consents).containsExactly(userConsent1, userConsent2);
        verify(jpaUserConsentRepository).findAllByTermsId(TERMS_ID);
    }

    @Test
    @DisplayName("사용자 동의 여부 확인 테스트")
    void hasUserConsented_ShouldReturnCorrectConsentStatus() {
        // given
        when(jpaUserConsentRepository.hasUserConsented(USER_ID, TERMS_ID)).thenReturn(true);

        // when
        boolean hasConsented = userConsentRepository.hasUserConsented(USER_ID, TERMS_ID);

        // then
        assertThat(hasConsented).isTrue();
        verify(jpaUserConsentRepository).hasUserConsented(USER_ID, TERMS_ID);
    }

    @Test
    @DisplayName("사용자의 약관 동의 수 카운트 테스트")
    void countConsentedTermsByUserIdAndTermsIds_ShouldReturnCorrectCount() {
        // given
        List<Long> termsIds = Arrays.asList(1L, 2L, 3L);
        when(jpaUserConsentRepository.countConsentedTermsByUserIdAndTermsIds(USER_ID, termsIds)).thenReturn(2L);

        // when
        long count = userConsentRepository.countConsentedTermsByUserIdAndTermsIds(USER_ID, termsIds);

        // then
        assertThat(count).isEqualTo(2);
        verify(jpaUserConsentRepository).countConsentedTermsByUserIdAndTermsIds(USER_ID, termsIds);
    }

    @Test
    @DisplayName("약관에 동의한 사용자 ID 조회 테스트")
    void findUserIdsByTermsIdAndConsented_ShouldReturnCorrectUserIds() {
        // given
        List<Long> userIds = Arrays.asList(1L, 2L, 3L);
        when(jpaUserConsentRepository.findUserIdsByTermsIdAndConsented(TERMS_ID)).thenReturn(userIds);

        // when
        List<Long> foundUserIds = userConsentRepository.findUserIdsByTermsIdAndConsented(TERMS_ID);

        // then
        assertThat(foundUserIds).hasSize(3);
        assertThat(foundUserIds).containsExactly(1L, 2L, 3L);
        verify(jpaUserConsentRepository).findUserIdsByTermsIdAndConsented(TERMS_ID);
    }
}