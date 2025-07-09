package com.pickone.domain.follow.service;

import com.pickone.domain.follow.dto.FollowRequest;
import com.pickone.domain.follow.dto.FollowResponse;
import com.pickone.domain.follow.entity.UserFollow;
import com.pickone.domain.follow.mapper.FollowMapper;
import com.pickone.domain.follow.repository.UserFollowJpaRepository;
import com.pickone.domain.notification.event.FollowedUserEvent;
import com.pickone.domain.user.entity.User;
import com.pickone.domain.user.model.vo.UserProfile;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FollowCommandServiceImplTest {

  @InjectMocks
  private FollowCommandServiceImpl followCommandService;

  @Mock
  private UserJpaRepository userRepository;

  @Mock
  private UserFollowJpaRepository followRepository;

  @Mock
  private ApplicationEventPublisher eventPublisher;

  @Mock
  private FollowMapper followMapper;

  private User fromUser;
  private User toUser;
  private UserProfile fromUserProfile;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    fromUserProfile = mock(UserProfile.class);
    fromUser = mock(User.class);
    toUser = mock(User.class);

    when(fromUser.getId()).thenReturn(1L);
    when(toUser.getId()).thenReturn(2L);
    when(fromUser.getProfile()).thenReturn(fromUserProfile);
    when(fromUserProfile.getNickname()).thenReturn("user1");
  }

  @Test
  void follow_success() {
    // given
    FollowRequest request = new FollowRequest(1L, 2L);
    UserFollow savedFollow = mock(UserFollow.class);
    FollowResponse expectedResponse = new FollowResponse(10L, 1L, 2L, "user1");

    when(userRepository.findById(1L)).thenReturn(Optional.of(fromUser));
    when(userRepository.findById(2L)).thenReturn(Optional.of(toUser));
    when(followRepository.save(any(UserFollow.class))).thenReturn(savedFollow);
    when(savedFollow.getId()).thenReturn(10L);
    when(followMapper.toDtoWithNickname(savedFollow, "user1")).thenReturn(expectedResponse);

    // when
    FollowResponse result = followCommandService.follow(request);

    // then
    assertEquals(10L, result.id());
    assertEquals("user1", result.nickname());

    verify(userRepository).findById(1L);
    verify(userRepository).findById(2L);
    verify(followRepository).save(any(UserFollow.class));
    verify(eventPublisher).publishEvent(any(FollowedUserEvent.class));
    verify(followMapper).toDtoWithNickname(savedFollow, "user1");
  }

  @Test
  void follow_fail_userNotFound() {
    // given
    FollowRequest request = new FollowRequest(1L, 99L);

    when(userRepository.findById(1L)).thenReturn(Optional.of(fromUser));
    when(userRepository.findById(99L)).thenReturn(Optional.empty());

    // when & then
    BusinessException exception = assertThrows(BusinessException.class, () -> {
      followCommandService.follow(request);
    });

    assertEquals(ErrorCode.USER_INFO_NOT_FOUND, exception.getErrorCode());

    verify(userRepository).findById(1L);
    verify(userRepository).findById(99L);
    verifyNoInteractions(followRepository, eventPublisher, followMapper);
  }

  @Test
  void unfollow_success() {
    // given
    FollowRequest request = new FollowRequest(1L, 2L);

    // when
    followCommandService.unfollow(request);

    // then
    verify(followRepository).deleteByFromUserIdAndToUserId(1L, 2L);
  }
}
