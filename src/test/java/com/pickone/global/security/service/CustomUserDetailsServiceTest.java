//package com.pickone.global.security.service;
//
//import com.pickone.domain.user.model.entity.UserEntity;
//import com.pickone.domain.user.repository.UserJpaRepository;
//import com.pickone.global.oauth2.service.UserFixture;
//import com.pickone.global.security.model.entity.UserPrincipal;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.*;
//
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class CustomUserDetailsServiceTest {
//
//  @InjectMocks
//  private CustomUserDetailsService sut;
//
//  @Mock
//  private UserJpaRepository userRepository;
//
//  @BeforeEach
//  void init() {
//    MockitoAnnotations.openMocks(this);
//  }
//
//  @Test
//  @DisplayName("loadUserByUsername: 유저가 존재하면 UserPrincipal 반환")
//  void loadUserByUsername_success() {
//    // given
//    String email = "user@example.com";
//    UserEntity user = UserFixture.createPasswordUser(email, "password123");
//    when(userRepository.findByProfileEmail(email)).thenReturn(Optional.of(user));
//
//    // when
//    UserDetails result = sut.loadUserByUsername(email);
//
//    // then
//    assertThat(result).isInstanceOf(UserPrincipal.class);
//    assertThat(result.getUsername()).isEqualTo(email);
//  }
//
//  @Test
//  @DisplayName("loadUserByUsername: 유저가 존재하지 않으면 예외 발생")
//  void loadUserByUsername_notFound() {
//    // given
//    String email = "nonexistent@example.com";
//    when(userRepository.findByProfileEmail(email)).thenReturn(Optional.empty());
//
//    // expect
//    assertThatThrownBy(() -> sut.loadUserByUsername(email))
//        .isInstanceOf(UsernameNotFoundException.class)
//        .hasMessageContaining("사용자를 찾을 수 없음");
//  }
//}
