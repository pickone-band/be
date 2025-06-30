package com.pickone.domain.user.service;

import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.domain.user.model.vo.UserPreference;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.global.common.enums.Genre;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserPreferenceService {

  private final UserJpaRepository userRepository;

  /**
   * 회원 선호 장르 업데이트
   * @param user 기존 UserEntity
   * @param genres 사용자 입력 장르 리스트
   */
  @Transactional
  public void update(UserEntity user, List<Genre> genres) {
    if (genres == null) {
      return;
    }

    UserPreference updatedPreference = user.getPreference().updateGenres(genres);
    user.updatePreference(updatedPreference);

    userRepository.save(user);
  }
}
