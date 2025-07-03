package com.pickone.domain.user.service;

import com.pickone.domain.user.dto.UserResponseDto;
import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.domain.user.model.mapper.UserMapper;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserQueryServiceImplTest {

  @Mock private UserJpaRepository userRepository;
  @InjectMocks private UserQueryServiceImpl sut;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("유저 정보 정상 반환")
  void getUser_success() {
    Long userId = 1L;
    UserEntity user = mock(UserEntity.class);
    UserResponseDto dto = mock(UserResponseDto.class);

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    try (MockedStatic<UserMapper> staticMapper = mockStatic(UserMapper.class)) {
      staticMapper.when(() -> UserMapper.toDto(user)).thenReturn(dto);

      UserResponseDto result = sut.getUser(userId);

      assertThat(result).isSameAs(dto);
      verify(userRepository).findById(userId);
    }
  }

  @Test
  @DisplayName("유저가 없으면 예외")
  void getUser_notFound() {
    Long userId = 2L;
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> sut.getUser(userId))
        .isInstanceOf(BusinessException.class);

  }
}
