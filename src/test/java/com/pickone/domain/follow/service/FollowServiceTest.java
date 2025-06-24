package com.pickone.domain.follow.service;

import com.pickone.domain.follow.model.entity.UserFollow;
import com.pickone.domain.follow.repository.UserFollowJpaRepository;
import com.pickone.domain.user.model.domain.Gender;
import com.pickone.domain.user.model.domain.Role;
import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.global.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FollowServiceTest {

    @InjectMocks
    private FollowService followService;

    @Mock
    private UserJpaRepository userJpaRepository;

    @Mock
    private UserFollowJpaRepository userFollowJpaRepository;

    @Mock private ApplicationEventPublisher eventPublisher;

    UserEntity userA;
    UserEntity userB;

    @BeforeEach
    void setUp() {
        userA = UserEntity.builder()
                .email("a@example.com")
                .password("pass")
                .nickname("A")
                .gender(Gender.MALE)
                .birthDate(LocalDate.of(1990, 1, 1))
                .role(Role.USER)
                .isPublic(true)
                .isOauth(false)
                .isVerified(true)
                .genres(List.of())
                .build();

        userB = UserEntity.builder()
                .email("b@example.com")
                .password("pass")
                .nickname("B")
                .gender(Gender.FEMALE)
                .birthDate(LocalDate.of(1995, 2, 2))
                .role(Role.USER)
                .isPublic(true)
                .isOauth(false)
                .isVerified(true)
                .genres(List.of())
                .build();
    }

    @Test
    @DisplayName("유저는 다른 유저를 팔로우할 수 있다")
    void follow_success() {
        when(userJpaRepository.findById(1L)).thenReturn(Optional.of(userA));
        when(userJpaRepository.findById(2L)).thenReturn(Optional.of(userB));
        when(userFollowJpaRepository.existsByFollowerAndFollowing(userA, userB)).thenReturn(false);

        followService.follow(1L, 2L);

        verify(userFollowJpaRepository).save(any(UserFollow.class));
    }

    @Test
    @DisplayName("자기 자신은 팔로우할 수 없다")
    void cannot_follow_self() {
//        when(userJpaRepository.findById(1L)).thenReturn(Optional.of(userA));

        assertThatThrownBy(() -> followService.follow(1L, 1L))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("이미 팔로우한 유저는 중복 팔로우할 수 없다")
    void already_following() {
        when(userJpaRepository.findById(1L)).thenReturn(Optional.of(userA));
        when(userJpaRepository.findById(2L)).thenReturn(Optional.of(userB));
        when(userFollowJpaRepository.existsByFollowerAndFollowing(userA, userB)).thenReturn(true);

        assertThatThrownBy(() -> followService.follow(1L, 2L))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("팔로우 관계가 존재하면 언팔로우할 수 있다")
    void unfollow_success() {
        when(userJpaRepository.findById(1L)).thenReturn(Optional.of(userA));
        when(userJpaRepository.findById(2L)).thenReturn(Optional.of(userB));
        UserFollow relation = new UserFollow(userA, userB);
        when(userFollowJpaRepository.findByFollowerAndFollowing(userA, userB))
                .thenReturn(Optional.of(relation));

        followService.unfollow(1L, 2L);

        verify(userFollowJpaRepository).delete(relation);
    }

    @Test
    @DisplayName("팔로우 관계가 존재하지 않으면 언팔로우할 수 없다")
    void unfollow_not_existing_relation() {
        when(userJpaRepository.findById(1L)).thenReturn(Optional.of(userA));
        when(userJpaRepository.findById(2L)).thenReturn(Optional.of(userB));
        when(userFollowJpaRepository.findByFollowerAndFollowing(userA, userB))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> followService.unfollow(1L, 2L))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("팔로워 목록 조회")
    void getFollowers_success() {
        when(userJpaRepository.findById(2L)).thenReturn(Optional.of(userB));
        when(userFollowJpaRepository.findAllByFollowing(userB)).thenReturn(List.of(
                new UserFollow(userA, userB)
        ));

        List<UserEntity> result = followService.getFollowers(2L);

        assertEquals(1, result.size());
        assertEquals("A", result.get(0).getNickname());
    }

    @Test
    @DisplayName("팔로잉 목록 조회")
    void getFollowings_success() {
        when(userJpaRepository.findById(1L)).thenReturn(Optional.of(userA));
        when(userFollowJpaRepository.findAllByFollower(userA)).thenReturn(List.of(
                new UserFollow(userA, userB)
        ));

        List<UserEntity> result = followService.getFollowings(1L);

        assertEquals(1, result.size());
        assertEquals("B", result.get(0).getNickname());
    }

    @Test
    @DisplayName("팔로워 조회 - 유저 없음")
    void getFollowers_userNotFound() {
        when(userJpaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> followService.getFollowers(99L))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("팔로잉 조회 - 유저 없음")
    void getFollowings_userNotFound() {
        when(userJpaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> followService.getFollowings(99L))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    @DisplayName("팔로우 상태 여부를 확인할 수 있다")
    void is_following_true() {
        when(userJpaRepository.findById(1L)).thenReturn(Optional.of(userA));
        when(userJpaRepository.findById(2L)).thenReturn(Optional.of(userB));
        when(userFollowJpaRepository.existsByFollowerAndFollowing(userA, userB)).thenReturn(true);

        boolean result = followService.isFollowing(1L, 2L);

        assertTrue(result);
    }

    @Test
    @DisplayName("팔로우하지 않은 경우 false를 반환한다")
    void is_following_false() {
        when(userJpaRepository.findById(1L)).thenReturn(Optional.of(userA));
        when(userJpaRepository.findById(2L)).thenReturn(Optional.of(userB));
        when(userFollowJpaRepository.existsByFollowerAndFollowing(userA, userB)).thenReturn(false);

        boolean result = followService.isFollowing(1L, 2L);

        assertFalse(result);
    }

}
