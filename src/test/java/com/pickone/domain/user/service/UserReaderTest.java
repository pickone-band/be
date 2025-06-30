package com.pickone.domain.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.pickone.domain.user.dto.UserSearchConditionDto;
import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.domain.user.repository.UserQueryDslRepository;
import com.pickone.global.exception.BusinessException;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class UserReaderTest {

  @Mock
  private UserJpaRepository userRepository;
  @Mock private UserQueryDslRepository userQueryDslRepository;
  @InjectMocks
  private UserReader userReader;

  @Test
  void findById_found() {
    UserEntity user = mock(UserEntity.class);
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));

    UserEntity found = userReader.findById(1L);

    assertEquals(user, found);
  }

  @Test
  void findById_notFound_throws() {
    when(userRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(BusinessException.class, () -> userReader.findById(1L));
  }

  @Test
  void searchUsers_callsQueryDslRepo() {
    Pageable pageable = Pageable.unpaged();
    UserSearchConditionDto cond = mock(UserSearchConditionDto.class);
    when(cond.getKeyword()).thenReturn("test");
    when(cond.getOnlyPublic()).thenReturn(true);

    when(userQueryDslRepository.searchByKeywordAndPublic("test", true, pageable))
        .thenReturn(Page.empty());

    Page<UserEntity> page = userReader.searchUsers(cond, pageable);

    assertNotNull(page);
  }
}
