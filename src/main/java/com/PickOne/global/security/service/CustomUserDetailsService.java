package com.PickOne.global.security.service;

import com.PickOne.domain.user.model.domain.Email;
import com.PickOne.domain.user.repository.UserRepository;
import com.PickOne.global.security.model.entity.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {

        return userRepository.findByEmail(Email.of(email))
                .map(UserPrincipal::from)
                .orElseThrow(() -> new UsernameNotFoundException("해당 이메일의 사용자가 없습니다: " + email));
    }
}
