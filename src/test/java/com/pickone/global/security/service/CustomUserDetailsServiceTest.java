package com.pickone.global.security.service;

import com.pickone.domain.user.entity.User;
import com.pickone.domain.user.model.domain.Gender;
import com.pickone.domain.user.model.domain.Role;
import com.pickone.domain.user.model.vo.*;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.global.common.enums.Mbti;
import com.pickone.global.security.entity.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

  private CustomUserDetailsService service;
  private UserJpaRepository userRepository;

  @BeforeEach
  void setUp() {
    userRepository = mock(UserJpaRepository.class);
    service = new CustomUserDetailsService(userRepository);
  }

  @Test
  void loadUserByUsername_existingUser_returnsUserPrincipal() {
    // given
    String email = "user@example.com";
    User user = User.create(email, "pw", "nick", Gender.MALE, LocalDate.now(), Mbti.INFP, List.of(), null, null);

    when(userRepository.findByProfileEmail(email)).thenReturn(Optional.of(user));

    // when
    UserPrincipal principal = (UserPrincipal) service.loadUserByUsername(email);

    // then
    assertNotNull(principal);
    assertEquals(email, principal.getEmail());
    verify(userRepository).findByProfileEmail(email);
  }

  @Test
  void loadUserByUsername_notFound_shouldThrow() {
    // given
    String email = "notfound@example.com";
    when(userRepository.findByProfileEmail(email)).thenReturn(Optional.empty());

    // expect
    assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername(email));
    verify(userRepository).findByProfileEmail(email);
  }
}
