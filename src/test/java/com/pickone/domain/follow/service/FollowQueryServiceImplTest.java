package com.pickone.domain.follow.service;

import com.pickone.domain.follow.dto.FollowResponse;
import com.pickone.domain.follow.entity.UserFollow;
import com.pickone.domain.follow.mapper.FollowMapper;
import com.pickone.domain.follow.repository.UserFollowJpaRepository;
import com.pickone.domain.user.entity.User;
import com.pickone.domain.user.model.vo.UserProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FollowQueryServiceImplTest {

  @InjectMocks
  private FollowQueryServiceImpl followQueryService;

  @Mock
  private UserFollowJpaRepository followRepository;

  @Mock
  private FollowMapper followMapper;

  private User fromUser;
  private User toUser;
  private UserProfile fromUserProfile;
  private UserProfile toUserProfile;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    fromUserProfile = mock(UserProfile.class);
    fromUser = mock(User.class);
    when(fromUser.getProfile()).thenReturn(fromUserProfile);
    when(fromUserProfile.getNickname()).thenReturn("팔로워닉네임");

    toUserProfile = mock(UserProfile.class);
    toUser = mock(User.class);
    when(toUser.getProfile()).thenReturn(toUserProfile);
    when(toUserProfile.getNickname()).thenReturn("팔로잉닉네임");
  }

  @Test
  void getFollowers_success() {
    // given
    Long userId = 1L;
    UserFollow follow = mock(UserFollow.class);
    FollowResponse expectedResponse = new FollowResponse(10L, 2L, userId, "팔로워닉네임");

    when(followRepository.findByToUserId(userId)).thenReturn(List.of(follow));
    when(follow.getFromUser()).thenReturn(fromUser);
    when(followMapper.toDtoWithNickname(follow, "팔로워닉네임")).thenReturn(expectedResponse);

    // when
    List<FollowResponse> result = followQueryService.getFollowers(userId);

    // then
    assertEquals(1, result.size());
    assertEquals(expectedResponse, result.get(0));
    verify(followRepository).findByToUserId(userId);
    verify(followMapper).toDtoWithNickname(follow, "팔로워닉네임");
  }

  @Test
  void getFollowings_success() {
    // given
    Long userId = 1L;
    UserFollow follow = mock(UserFollow.class);
    FollowResponse expectedResponse = new FollowResponse(20L, userId, 3L, "팔로잉닉네임");

    when(followRepository.findByFromUserId(userId)).thenReturn(List.of(follow));
    when(follow.getToUser()).thenReturn(toUser);
    when(followMapper.toDtoWithNickname(follow, "팔로잉닉네임")).thenReturn(expectedResponse);

    // when
    List<FollowResponse> result = followQueryService.getFollowings(userId);

    // then
    assertEquals(1, result.size());
    assertEquals(expectedResponse, result.get(0));
    verify(followRepository).findByFromUserId(userId);
    verify(followMapper).toDtoWithNickname(follow, "팔로잉닉네임");
  }

  @Test
  void isFollowing_true() {
    // given
    Long fromUserId = 1L;
    Long toUserId = 2L;

    when(followRepository.existsByFromUserIdAndToUserId(fromUserId, toUserId)).thenReturn(true);

    // when
    boolean result = followQueryService.isFollowing(fromUserId, toUserId);

    // then
    assertTrue(result);
    verify(followRepository).existsByFromUserIdAndToUserId(fromUserId, toUserId);
  }

  @Test
  void isFollowing_false() {
    // given
    Long fromUserId = 1L;
    Long toUserId = 3L;

    when(followRepository.existsByFromUserIdAndToUserId(fromUserId, toUserId)).thenReturn(false);

    // when
    boolean result = followQueryService.isFollowing(fromUserId, toUserId);

    // then
    assertFalse(result);
    verify(followRepository).existsByFromUserIdAndToUserId(fromUserId, toUserId);
  }
}
