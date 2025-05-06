package com.PickOne.user.repository;

import com.PickOne.domain.user.model.domain.Email;
import com.PickOne.domain.user.model.domain.Password;
import com.PickOne.domain.user.model.domain.User;
import com.PickOne.domain.user.model.entity.UserEntity;
import com.PickOne.domain.user.repository.JpaUserRepositoryImpl;
import com.PickOne.domain.user.repository.UserJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * JpaUserRepositoryImpl 클래스에 대한 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class JpaUserRepositoryImplTest {

    @Mock
    private UserJpaRepository userJpaRepository;

    @InjectMocks
    private JpaUserRepositoryImpl userRepository;

    // 테스트에 사용할 샘플 데이터
    private final Long userId = 1L;
    private final String userEmail = "test@example.com";
    private final String userPassword = "encodedPassword";

    /**
     * 사용자 저장 기능 테스트
     */
    @Test
    @DisplayName("사용자 저장 테스트 - 성공 케이스")
    void save_Success() {
        // given
        User user = User.create(Email.of(userEmail), Password.of(userPassword));
        User expectedUser = User.of(userId, Email.of(userEmail), Password.ofEncoded(userPassword));

        // 실제 UserEntity 객체를 생성하여 저장 동작 시뮬레이션
        UserEntity savedEntity = mock(UserEntity.class);
        when(savedEntity.toDomain()).thenReturn(expectedUser);
        when(userJpaRepository.save(any(UserEntity.class))).thenReturn(savedEntity);

        // when
        User savedUser = userRepository.save(user);

        // then
        verify(userJpaRepository, times(1)).save(any(UserEntity.class));
        assertThat(savedUser).isEqualTo(expectedUser);
    }

    /**
     * ID로 사용자 조회 기능 테스트 - 성공 케이스
     */
    @Test
    @DisplayName("ID로 사용자 조회 테스트 - 성공 케이스")
    void findById_Success() {
        // given
        User expectedUser = User.of(userId, Email.of(userEmail), Password.ofEncoded(userPassword));

        // 모의 엔티티 객체 생성 및 설정
        UserEntity mockEntity = mock(UserEntity.class);
        when(mockEntity.toDomain()).thenReturn(expectedUser);

        when(userJpaRepository.findById(userId)).thenReturn(Optional.of(mockEntity));

        // when
        Optional<User> foundUser = userRepository.findById(userId);

        // then
        verify(userJpaRepository, times(1)).findById(userId);
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get()).isEqualTo(expectedUser);
    }

    /**
     * 이메일로 사용자 조회 기능 테스트 - 성공 케이스
     */
    @Test
    @DisplayName("이메일로 사용자 조회 테스트 - 성공 케이스")
    void findByEmail_Success() {
        // given
        User expectedUser = User.of(userId, Email.of(userEmail), Password.ofEncoded(userPassword));

        // 모의 엔티티 객체 생성 및 설정
        UserEntity mockEntity = mock(UserEntity.class);
        when(mockEntity.toDomain()).thenReturn(expectedUser);

        when(userJpaRepository.findByEmail(userEmail)).thenReturn(Optional.of(mockEntity));

        // when
        Optional<User> foundUser = userRepository.findByEmail(userEmail);

        // then
        verify(userJpaRepository, times(1)).findByEmail(userEmail);
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get()).isEqualTo(expectedUser);
    }

    /**
     * ID로 사용자 조회 기능 테스트 - 실패 케이스
     */
    @Test
    @DisplayName("존재하지 않는 ID로 사용자 조회 테스트 - 실패 케이스")
    void findById_UserNotFound() {
        // given
        Long nonExistentId = 999L;
        when(userJpaRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // when
        Optional<User> foundUser = userRepository.findById(nonExistentId);

        // then
        verify(userJpaRepository, times(1)).findById(nonExistentId);
        assertThat(foundUser).isEmpty();
    }

    /**
     * 이메일로 사용자 조회 기능 테스트 - 실패 케이스
     */
    @Test
    @DisplayName("존재하지 않는 이메일로 사용자 조회 테스트 - 실패 케이스")
    void findByEmail_UserNotFound() {
        // given
        String nonExistentEmail = "nonexistent@example.com";
        when(userJpaRepository.findByEmail(nonExistentEmail)).thenReturn(Optional.empty());

        // when
        Optional<User> foundUser = userRepository.findByEmail(nonExistentEmail);

        // then
        verify(userJpaRepository, times(1)).findByEmail(nonExistentEmail);
        assertThat(foundUser).isEmpty();
    }
}