package com.pickone.domain.follow.service;

import com.pickone.domain.follow.dto.FollowResponse;
import com.pickone.domain.follow.model.entity.UserFollow;
import com.pickone.domain.follow.model.mapper.FollowMapper;
import com.pickone.domain.follow.repository.UserFollowJpaRepository;
import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.domain.user.model.vo.UserProfile;
import com.pickone.domain.user.repository.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class FollowQueryServiceImplTest {

  @Mock private UserFollowJpaRepository followRepository;
  @Mock private UserJpaRepository userRepository;
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

    when(followRepository.findByToUserId(userId)).thenReturn(entities);
    when(uf1.getFromUserId()).thenReturn(10L);
    when(uf2.getFromUserId()).thenReturn(20L);

    UserEntity user1 = mock(UserEntity.class);
    UserEntity user2 = mock(UserEntity.class);
    UserProfile profile1 = mock(UserProfile.class);
    UserProfile profile2 = mock(UserProfile.class);
    when(user1.getProfile()).thenReturn(profile1);
    when(user2.getProfile()).thenReturn(profile2);
    when(profile1.getNickname()).thenReturn("nick1");
    when(profile2.getNickname()).thenReturn("nick2");

    when(userRepository.findById(10L)).thenReturn(Optional.of(user1));
    when(userRepository.findById(20L)).thenReturn(Optional.of(user2));

    FollowResponse dto1 = mock(FollowResponse.class);
    FollowResponse dto2 = mock(FollowResponse.class);

    try (MockedStatic<FollowMapper> mapperStatic = mockStatic(FollowMapper.class)) {
      mapperStatic.when(() -> FollowMapper.toDtoWithNickname(uf1, "nick1")).thenReturn(dto1);
      mapperStatic.when(() -> FollowMapper.toDtoWithNickname(uf2, "nick2")).thenReturn(dto2);

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

    when(followRepository.findByFromUserId(userId)).thenReturn(entities);
    when(uf1.getToUserId()).thenReturn(30L);

    UserEntity user1 = mock(UserEntity.class);
    UserProfile profile1 = mock(UserProfile.class);
    when(user1.getProfile()).thenReturn(profile1);
    when(profile1.getNickname()).thenReturn("toNick");

    when(userRepository.findById(30L)).thenReturn(Optional.of(user1));

    FollowResponse dto1 = mock(FollowResponse.class);

    try (MockedStatic<FollowMapper> mapperStatic = mockStatic(FollowMapper.class)) {
      mapperStatic.when(() -> FollowMapper.toDtoWithNickname(uf1, "toNick")).thenReturn(dto1);

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
