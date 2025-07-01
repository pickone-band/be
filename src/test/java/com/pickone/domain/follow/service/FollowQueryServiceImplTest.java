package com.pickone.domain.follow.service;

import com.pickone.domain.follow.dto.FollowResponse;
import com.pickone.domain.follow.model.entity.UserFollow;
import com.pickone.domain.follow.model.mapper.FollowMapper;
import com.pickone.domain.follow.repository.UserFollowJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class FollowQueryServiceImplTest {

  @Mock private UserFollowJpaRepository followRepository;
  @InjectMocks private FollowQueryServiceImpl sut;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("getFollowers: 팔로워 리스트 반환")
  void getFollowers_success() {
    Long userId = 1L;
    UserFollow uf1 = mock(UserFollow.class);
    UserFollow uf2 = mock(UserFollow.class);
    List<UserFollow> entities = Arrays.asList(uf1, uf2);

    FollowResponse dto1 = mock(FollowResponse.class);
    FollowResponse dto2 = mock(FollowResponse.class);

    when(followRepository.findByToUserId(userId)).thenReturn(entities);

    try (MockedStatic<FollowMapper> mapperStatic = mockStatic(FollowMapper.class)) {
      mapperStatic.when(() -> FollowMapper.toDto(uf1)).thenReturn(dto1);
      mapperStatic.when(() -> FollowMapper.toDto(uf2)).thenReturn(dto2);

      List<FollowResponse> result = sut.getFollowers(userId);

      assertThat(result).containsExactly(dto1, dto2);
      verify(followRepository).findByToUserId(userId);
    }
  }

  @Test
  @DisplayName("getFollowers: 팔로워가 없으면 빈 리스트")
  void getFollowers_empty() {
    Long userId = 1L;
    when(followRepository.findByToUserId(userId)).thenReturn(Collections.emptyList());

    List<FollowResponse> result = sut.getFollowers(userId);

    assertThat(result).isEmpty();
    verify(followRepository).findByToUserId(userId);
  }

  @Test
  @DisplayName("getFollowings: 팔로잉 리스트 반환")
  void getFollowings_success() {
    Long userId = 2L;
    UserFollow uf1 = mock(UserFollow.class);
    List<UserFollow> entities = List.of(uf1);
    FollowResponse dto1 = mock(FollowResponse.class);

    when(followRepository.findByFromUserId(userId)).thenReturn(entities);

    try (MockedStatic<FollowMapper> mapperStatic = mockStatic(FollowMapper.class)) {
      mapperStatic.when(() -> FollowMapper.toDto(uf1)).thenReturn(dto1);

      List<FollowResponse> result = sut.getFollowings(userId);

      assertThat(result).containsExactly(dto1);
      verify(followRepository).findByFromUserId(userId);
    }
  }

  @Test
  @DisplayName("getFollowings: 팔로잉이 없으면 빈 리스트")
  void getFollowings_empty() {
    Long userId = 2L;
    when(followRepository.findByFromUserId(userId)).thenReturn(Collections.emptyList());

    List<FollowResponse> result = sut.getFollowings(userId);

    assertThat(result).isEmpty();
    verify(followRepository).findByFromUserId(userId);
  }

  @Test
  @DisplayName("isFollowing: 팔로우 관계 존재(true)")
  void isFollowing_true() {
    Long from = 1L, to = 2L;
    when(followRepository.existsByFromUserIdAndToUserId(from, to)).thenReturn(true);

    boolean result = sut.isFollowing(from, to);

    assertThat(result).isTrue();
    verify(followRepository).existsByFromUserIdAndToUserId(from, to);
  }

  @Test
  @DisplayName("isFollowing: 팔로우 관계 없음(false)")
  void isFollowing_false() {
    Long from = 1L, to = 2L;
    when(followRepository.existsByFromUserIdAndToUserId(from, to)).thenReturn(false);

    boolean result = sut.isFollowing(from, to);

    assertThat(result).isFalse();
    verify(followRepository).existsByFromUserIdAndToUserId(from, to);
  }
}
