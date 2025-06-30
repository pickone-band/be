package com.pickone.domain.user.service;

import com.pickone.domain.consent.service.ConsentService;
import com.pickone.domain.user.dto.SignupRequestDto;
import com.pickone.domain.user.model.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserJoinService {

  private final UserCreator userCreator;
  private final UserInstrumentService userInstrumentService;
  private final ConsentService consentService;

  @Transactional
  public void join(SignupRequestDto requestDto) {
    UserEntity user = userCreator.createUser(requestDto); // 1. 유저 생성
    userInstrumentService.saveAll(user, requestDto.instruments()); // 2. 악기 매핑
    consentService.saveAll(user, requestDto.agreements()); // 3. 약관 동의 저장
  }
}
