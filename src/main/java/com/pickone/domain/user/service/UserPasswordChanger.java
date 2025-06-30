package com.pickone.domain.user.service;

import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserPasswordChanger {

  private final UserReader userReader;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public void changePassword(Long userId, String rawPassword) {
    log.info("비밀번호 변경 요청: userId={}", userId);

    if (rawPassword == null || rawPassword.length() < 6) {
      throw new BusinessException(ErrorCode.INVALID_PASSWORD_FORMAT);
    }

    UserEntity user = userReader.findById(userId);
    user.updatePassword(passwordEncoder.encode(rawPassword));
    log.info("비밀번호 변경 완료: userId={}", userId);
  }
}
