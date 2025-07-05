package com.pickone.domain.follow.service;

import com.pickone.domain.follow.dto.FollowRequest;
import com.pickone.domain.follow.dto.FollowResponse;
import com.pickone.domain.follow.model.entity.UserFollow;
import com.pickone.domain.follow.model.mapper.FollowMapper;
import com.pickone.domain.follow.repository.UserFollowJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.context.ApplicationEventPublisher;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class FollowCommandServiceImplTest {

  @Mock private UserFollowJpaRepository followRepository;
  @Mock private ApplicationEventPublisher eventPublisher;
  @InjectMocks private FollowCommandServiceImpl sut;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Nested
  @DisplayName("follow")
  class Follow {

    @Test
    @DisplayName("정상 팔로우 저장 및 반환")
    void follow_success() {
      // given
      Long fromId = 1L, toId = 2L;
      FollowRequest req = new FollowRequest(fromId, toId);

      when(followRepository.existsByFromUserIdAndToUserId(fromId, toId)).thenReturn(false);

      UserFollow entity = mock(UserFollow.class);
      UserFollow saved = mock(UserFollow.class);
      FollowResponse expectedDto = mock(FollowResponse.class);

      // 정적 factory
      try (MockedStatic<UserFollow> mockedStatic = mockStatic(UserFollow.class)) {
        mockedStatic.when(() -> UserFollow.of(fromId, toId)).thenReturn(entity);

        when(followRepository.save(entity)).thenReturn(saved);

        // FollowMapper static 처리
        try (MockedStatic<FollowMapper> mapperStatic = mockStatic(FollowMapper.class)) {
          mapperStatic.when(() -> FollowMapper.toDto(saved)).thenReturn(expectedDto);

          // when
          FollowResponse result = sut.follow(req);

          // then
          assertThat(result).isSameAs(expectedDto);
          verify(followRepository).existsByFromUserIdAndToUserId(fromId, toId);
          verify(followRepository).save(entity);
        }
      }
    }

 }

  @Nested
  @DisplayName("unfollow")
  class Unfollow {
    @Test
    @DisplayName("정상 언팔로우 동작")
    void unfollow_success() {
      Long fromId = 1L, toId = 2L;
      FollowRequest req = new FollowRequest(fromId, toId);

      // when
      sut.unfollow(req);

      // then
      verify(followRepository).deleteByFromUserIdAndToUserId(fromId, toId);
    }
  }
}
