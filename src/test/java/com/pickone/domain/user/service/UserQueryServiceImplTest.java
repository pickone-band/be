package com.pickone.domain.user.service;

import com.pickone.domain.user.dto.UserResponse;
import com.pickone.domain.user.entity.User;
import com.pickone.domain.user.mapper.UserMapper;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserQueryServiceImplTest {

  @InjectMocks
  private UserQueryServiceImpl userQueryService;

  @Mock
  private UserJpaRepository userRepository;

  @Mock
  private UserMapper userMapper;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void getUserById_success() {
    Long userId = 1L;

    User mockUser = mock(User.class);
    UserResponse expectedResponse = mock(UserResponse.class);

    when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
    when(userMapper.toDto(mockUser)).thenReturn(expectedResponse);

    UserResponse result = userQueryService.getUser(userId);

    assertNotNull(result);
    assertEquals(expectedResponse, result);
  }

  @Test
  void getUserById_userNotFound_shouldThrowException() {
    Long userId = 99L;

    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    BusinessException ex = assertThrows(BusinessException.class, () ->
        userQueryService.getUser(userId)
    );
    assertEquals(ErrorCode.USER_INFO_NOT_FOUND, ex.getErrorCode());
  }
}
