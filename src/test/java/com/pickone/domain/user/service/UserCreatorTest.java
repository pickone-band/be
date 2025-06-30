package com.pickone.domain.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pickone.domain.user.dto.SignupRequestDto;
import com.pickone.domain.user.model.domain.Gender;
import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.global.common.enums.Genre;
import com.pickone.global.common.enums.Mbti;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserCreatorTest {

  @Mock
  private UserJpaRepository userRepository;
  @Mock private PasswordEncoder passwordEncoder;
  @InjectMocks
  private UserCreator userCreator;

  @Test
  void createUser_success() {
    SignupRequestDto dto = new SignupRequestDto(
        "test@example.com", "password123", "nickname", LocalDate.of(1990,1,1),
        Gender.MALE, "01012345678", List.of(), List.of(Genre.ACOUSTIC), Mbti.INTJ,
        List.of()
    );
    String encodedPwd = "encodedPwd";
    when(passwordEncoder.encode(dto.password())).thenReturn(encodedPwd);

    UserEntity savedUser = UserEntity.of(dto.email(), encodedPwd, dto.nickname(),
        dto.gender(), dto.birthDate(), dto.mbti(), List.of(Genre.ACOUSTIC));
    when(userRepository.save(any(UserEntity.class))).thenReturn(savedUser);

    UserEntity result = userCreator.createUser(dto);

    verify(passwordEncoder).encode(dto.password());
    verify(userRepository).save(any(UserEntity.class));
    assertEquals(dto.email(), result.getProfile().getEmail());
    assertEquals(encodedPwd, result.getProfile().getPassword());
    assertEquals(dto.nickname(), result.getProfile().getNickname());
  }
}
