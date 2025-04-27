package com.PickOne.user.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.PickOne.user.model.domain.Email;
import com.PickOne.user.model.domain.Password;
import com.PickOne.user.model.domain.User;
import com.PickOne.user.model.entity.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JpaUserRepositoryTest {

    @Mock
    private UserJpaRepository userJpaRepository;

    @InjectMocks
    private JpaUserRepository jpaUserRepository;

    @Test
    @DisplayName("사용자 저장 성공")
    void saveUser() {
        // given
        User user = User.create(
                Email.of("test@example.com"),
                Password.of("Password1!")
        );

        UserEntity mockEntity = mock(UserEntity.class);
        User mockDomainUser = mock(User.class);

        when(userJpaRepository.save(any(UserEntity.class))).thenReturn(mockEntity);
        when(mockEntity.toDomain()).thenReturn(mockDomainUser);

        // when
        User savedUser = jpaUserRepository.save(user);

        // then
        assertNotNull(savedUser);
        assertEquals(mockDomainUser, savedUser);
        verify(userJpaRepository).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("ID로 사용자 찾기 성공")
    void findUserById() {
        // given
        Long userId = 1L;
        UserEntity mockEntity = mock(UserEntity.class);
        User mockUser = mock(User.class);

        when(userJpaRepository.findById(userId)).thenReturn(Optional.of(mockEntity));
        when(mockEntity.toDomain()).thenReturn(mockUser);

        // when
        Optional<User> foundUser = jpaUserRepository.findById(userId);

        // then
        assertTrue(foundUser.isPresent());
        assertEquals(mockUser, foundUser.get());
        verify(userJpaRepository).findById(userId);
    }

    @Test
    @DisplayName("이메일로 사용자 찾기 성공")
    void findUserByEmail() {
        // given
        String email = "test@example.com";
        UserEntity mockEntity = mock(UserEntity.class);
        User mockUser = mock(User.class);

        when(userJpaRepository.findByEmail(email)).thenReturn(Optional.of(mockEntity));
        when(mockEntity.toDomain()).thenReturn(mockUser);

        // when
        Optional<User> foundUser = jpaUserRepository.findByEmail(email);

        // then
        assertTrue(foundUser.isPresent());
        assertEquals(mockUser, foundUser.get());
        verify(userJpaRepository).findByEmail(email);
    }

    @Test
    @DisplayName("모든 사용자 찾기 성공")
    void findAllUsers() {
        // given
        UserEntity entity1 = mock(UserEntity.class);
        UserEntity entity2 = mock(UserEntity.class);
        User user1 = mock(User.class);
        User user2 = mock(User.class);

        when(userJpaRepository.findAll()).thenReturn(Arrays.asList(entity1, entity2));
        when(entity1.toDomain()).thenReturn(user1);
        when(entity2.toDomain()).thenReturn(user2);

        // when
        List<User> users = jpaUserRepository.findAll();

        // then
        assertEquals(2, users.size());
        assertEquals(user1, users.get(0));
        assertEquals(user2, users.get(1));
        verify(userJpaRepository).findAll();
    }

    @Test
    @DisplayName("사용자 삭제 성공")
    void deleteUser() {
        // given
        Long userId = 1L;
        User user = mock(User.class);
        UserEntity entity = mock(UserEntity.class);

        when(user.getId()).thenReturn(userId);
        when(userJpaRepository.findById(userId)).thenReturn(Optional.of(entity));

        // when
        jpaUserRepository.delete(user);

        // then
        verify(userJpaRepository).delete(entity);
    }
}