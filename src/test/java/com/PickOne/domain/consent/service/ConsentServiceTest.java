package com.PickOne.domain.consent.service;

import com.PickOne.domain.consent.dto.ConsentRequestDto;
import com.PickOne.domain.consent.model.entity.ConsentEntity;
import com.PickOne.domain.consent.repository.ConsentJpaRepository;
import com.PickOne.domain.term.model.entity.TermEntity;
import com.PickOne.domain.term.repository.TermJpaRepository;
import com.PickOne.domain.user.model.entity.UserEntity;
import com.PickOne.domain.user.repository.UserJpaRepository;
import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConsentService 단위 테스트")
class ConsentServiceTest {

    @Mock private ConsentJpaRepository consentRepository;
    @Mock private UserJpaRepository userRepository;
    @Mock private TermJpaRepository termRepository;

    @InjectMocks
    private ConsentService consentService;

    private final Long userId = 1L;
    private final Long termId = 1L;

    private ConsentRequestDto requestDto;

    @BeforeEach
    void setUp() {
        requestDto = new ConsentRequestDto(termId, true);
    }

    @Test
    @DisplayName("동의 저장 성공")
    void saveConsent_success() {
        // given
        UserEntity user = mock(UserEntity.class);
        TermEntity term = mock(TermEntity.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(termRepository.findById(termId)).thenReturn(Optional.of(term));
        when(consentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        ConsentEntity result = consentService.saveConsent(userId, termId, requestDto);

        // then
        assertEquals(user, result.getUser());
        assertEquals(term, result.getTerm());
        assertTrue(result.isConsented());
        assertNotNull(result.getConsentDate());
    }

    @Test
    @DisplayName("존재하지 않는 사용자로 저장 시도시 예외 발생")
    void saveConsent_userNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> consentService.saveConsent(userId, termId, requestDto)
        );

        assertEquals(ErrorCode.USER_INFO_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    @DisplayName("존재하지 않는 약관으로 저장 시도시 예외 발생")
    void saveConsent_termNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(mock(UserEntity.class)));
        when(termRepository.findById(termId)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(
                BusinessException.class,
                () -> consentService.saveConsent(userId, termId, requestDto)
        );

        assertEquals(ErrorCode.TERM_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    @DisplayName("사용자 동의 목록 조회")
    void getUserConsents() {
        ConsentEntity c1 = mock(ConsentEntity.class);
        ConsentEntity c2 = mock(ConsentEntity.class);

        when(consentRepository.findByUserId(userId)).thenReturn(List.of(c1, c2));

        List<ConsentEntity> results = consentService.getUserConsents(userId);

        assertEquals(2, results.size());
    }

    @Test
    @DisplayName("동의 여부: 이미 동의한 경우 true 반환")
    void hasConsented_true() {
        ConsentEntity consent = mock(ConsentEntity.class);
        when(consent.isConsented()).thenReturn(true);

        when(consentRepository.findByUserIdAndTermId(userId, termId))
                .thenReturn(Optional.of(consent));

        assertTrue(consentService.hasConsented(userId, termId));
    }

    @Test
    @DisplayName("동의 여부: 동의 이력이 없는 경우 false 반환")
    void hasConsented_false() {
        when(consentRepository.findByUserIdAndTermId(userId, termId))
                .thenReturn(Optional.empty());

        assertFalse(consentService.hasConsented(userId, termId));
    }

    @Test
    @DisplayName("동의 삭제가 정상적으로 호출됨")
    void deleteConsent() {
        consentService.deleteConsent(userId, termId);
        verify(consentRepository).deleteByUserIdAndTermId(userId, termId);
    }
}
