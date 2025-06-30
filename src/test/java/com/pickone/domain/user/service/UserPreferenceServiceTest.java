package com.pickone.domain.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.domain.user.model.vo.UserPreference;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.global.common.enums.Genre;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserPreferenceServiceTest {

  @Mock
  private UserJpaRepository userRepository;
  @InjectMocks
  private UserPreferenceService preferenceService;

  @Test
  void update_updatesUserPreference() {
    UserEntity user = mock(UserEntity.class);
    UserPreference preference = mock(UserPreference.class); // 추가!
    when(user.getPreference()).thenReturn(preference);      // 추가!

    List<Genre> genres = List.of(Genre.ACOUSTIC);
    preferenceService.update(user, genres);

    verify(user).updatePreference(any());
  }
}
