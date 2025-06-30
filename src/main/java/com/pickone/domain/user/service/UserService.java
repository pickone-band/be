package com.pickone.domain.user.service;

import com.pickone.domain.user.dto.SignupRequestDto;
import com.pickone.domain.user.dto.UserSearchConditionDto;
import com.pickone.domain.user.dto.UserUpdateRequestDto;
import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.domain.user.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserReader userReader;
  private final UserUpdater userUpdater;
  private final UserCreator userCreator;
  private final UserJoinService userJoinService;
  private final UserInstrumentService userInstrumentService;
  private final UserPreferenceService userPreferenceService;
  private final UserPasswordChanger userPasswordChanger;
  private final UserJpaRepository userJpaRepository;

  @Transactional(readOnly = true)
  public UserEntity findById(Long id) {
    return userReader.findById(id);
  }

  @Transactional(readOnly = true)
  public UserEntity findByEmail(String email) {
    return userReader.findByEmail(email);
  }

  @Transactional
  public UserEntity createUser(SignupRequestDto dto) {
    return userCreator.createUser(dto);
  }

  @Transactional
  public void joinUser(SignupRequestDto dto) {
    userJoinService.join(dto);
  }

  @Transactional
  public void updateUser(Long userId, UserUpdateRequestDto dto) {
    userUpdater.updateUser(userId, dto);
  }

  @Transactional
  public void changePassword(Long userId, String rawPassword) {
    userPasswordChanger.changePassword(userId, rawPassword);
  }

  @Transactional
  public void deleteUser(Long id) {
    UserEntity user = userReader.findById(id);
    userJpaRepository.delete(user);
  }

  @Transactional(readOnly = true)
  public Page<UserEntity> searchUsers(UserSearchConditionDto condition, Pageable pageable) {
    return userReader.searchUsers(condition, pageable);
  }
}
