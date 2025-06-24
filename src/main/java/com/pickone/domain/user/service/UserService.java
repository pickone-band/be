package com.pickone.domain.user.service;

import com.pickone.domain.user.dto.UserSearchConditionDto;
import com.pickone.domain.user.dto.UserUpdateRequestDto;
import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.domain.user.model.entity.UserInstrumentEntity;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.domain.user.repository.UserQueryDslRepository;
import com.pickone.global.common.enums.Genre;
import com.pickone.global.common.enums.Instrument;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

  private final UserJpaRepository userJpaRepository;
  private final UserQueryDslRepository userQueryDslRepository;
  private final PasswordEncoder passwordEncoder;

  @Transactional(readOnly = true)
  public UserEntity findById(Long id) {
    log.info("사용자 조회: id={}", id);
    return userJpaRepository.findById(id)
        .orElseThrow(() -> {
          log.warn("사용자 정보 없음: id={}", id);
          return new BusinessException(ErrorCode.USER_INFO_NOT_FOUND);
        });
  }

  @Transactional(readOnly = true)
  public UserEntity findByEmail(String email) {
    log.info("이메일로 사용자 조회: {}", email);
    return userJpaRepository.findByEmail(email)
        .orElseThrow(() -> {
          log.warn("이메일로 사용자 조회 실패: {}", email);
          return new BusinessException(ErrorCode.USER_INFO_NOT_FOUND);
        });
  }

  @Transactional
  public void updateUser(Long id, UserUpdateRequestDto dto) {
    log.info("사용자 정보 수정 시작: id={}", id);
    UserEntity user = findById(id);

    user.updateNickname(dto.getNickname());
    user.updateProfileImage(dto.getProfileImageUrl());
    user.updateVisibility(dto.getIsPublic());
    user.updateMbti(dto.getMbti());

    if (dto.getGenres() != null) {
      List<Genre> genres = new ArrayList<>(dto.getGenres());
      user.updateGenres(genres);
    }

    if (dto.getInstruments() != null) {
      List<UserInstrumentEntity> instrumentEntities = dto.getInstruments().stream()
          .map(i -> UserInstrumentEntity.builder()
              .instrument(i.instrument())
              .proficiency(i.proficiency())
              .build())
          .collect(Collectors.toList());
      user.updateInstruments(instrumentEntities);
    }

    log.info("사용자 정보 수정 완료: id={}", id);
  }

  @Transactional
  public void updatePassword(Long id, String rawPassword) {
    log.info("비밀번호 변경 요청: id={}", id);
    UserEntity user = findById(id);
    String encoded = passwordEncoder.encode(rawPassword);
    user.updatePassword(encoded);
  }

  @Transactional
  public void deleteUser(Long id) {
    log.info("사용자 삭제 요청: id={}", id);
    if (!userJpaRepository.existsById(id)) {
      log.warn("삭제 실패: 사용자 없음 id={}", id);
      throw new BusinessException(ErrorCode.USER_INFO_NOT_FOUND);
    }
    userJpaRepository.deleteById(id);
    log.info("사용자 삭제 완료: id={}", id);
  }

  @Transactional(readOnly = true)
  public Page<UserEntity> searchUsers(UserSearchConditionDto condition, Pageable pageable) {
    log.info("사용자 검색 실행: condition={}, pageable={}", condition, pageable);
    return userQueryDslRepository.searchUsers(condition, pageable);
  }

  @Transactional(readOnly = true)
  public Page<UserEntity> findUsersByInstrument(String rawInstrument, Pageable pageable) {
    Instrument instrument = Instrument.valueOf(rawInstrument);
    log.info("악기로 사용자 검색: instrument={}", instrument);
    return userQueryDslRepository.searchByInstrument(instrument, pageable);
  }

  @Transactional(readOnly = true)
  public Page<UserEntity> findUsersByGenre(String rawGenre, Pageable pageable) {
    Genre genre = Genre.valueOf(rawGenre);
    log.info("장르로 사용자 검색: genre={}", genre);
    return userQueryDslRepository.searchByGenre(genre, pageable);
  }
}
