package com.pickone.global.security.service;

import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.global.security.model.entity.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UserJpaRepository userJpaRepository;

  @Override
  public UserDetails loadUserByUsername(String email) {
    log.debug("UserDetails 조회 요청: email={}", email);
    return userJpaRepository.findByProfile_Email(email)
        .map(UserPrincipal::from)
        .orElseThrow(() -> {
          log.warn("UserDetails 조회 실패: email={}", email);
          return new UsernameNotFoundException("해당 이메일의 사용자가 없습니다: " + email);
        });
  }
}
