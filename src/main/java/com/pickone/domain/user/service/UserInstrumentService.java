package com.pickone.domain.user.service;

import com.pickone.domain.user.dto.InstrumentInfoDto;
import com.pickone.domain.user.model.entity.UserEntity;
import com.pickone.domain.user.model.entity.UserInstrumentEntity;
import com.pickone.domain.user.repository.UserInstrumentJpaRepository;
import com.pickone.global.common.enums.Instrument;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserInstrumentService {

  private final UserInstrumentJpaRepository instrumentRepository;

  /**
   * 회원가입 시 Dto 기반 악기 정보 저장
   */
  @Transactional
  public void saveAll(UserEntity user, List<InstrumentInfoDto> instrumentDtos) {
    if (instrumentDtos == null || instrumentDtos.isEmpty()) return;

    List<UserInstrumentEntity> entities = instrumentDtos.stream()
        .map(dto -> UserInstrumentEntity.create(user, dto.type(), dto.level()))
        .toList();

    instrumentRepository.saveAll(entities);
  }

  @Transactional
  public void update(UserEntity user, List<InstrumentInfoDto> instrumentDtos) {
    if (instrumentDtos == null) {
      return;
    }

    // 1. 기존 악기 엔티티 리스트를 가져옴
    List<UserInstrumentEntity> existingInstruments = user.getInstruments();

    // 2. 새로 입력받은 악기 목록에 없는 기존 악기 삭제
    existingInstruments.removeIf(inst ->
        instrumentDtos.stream()
            .noneMatch(dto -> dto.type() == inst.getInstrument())
    );

    // 3. 기존 악기와 매칭되지 않는 신규 악기는 추가
    for (InstrumentInfoDto dto : instrumentDtos) {
      boolean exists = existingInstruments.stream()
          .anyMatch(inst -> inst.getInstrument() == dto.type());

      if (!exists) {
        UserInstrumentEntity newInstrument = UserInstrumentEntity.create(user, dto.type(), dto.level());
        existingInstruments.add(newInstrument);
        instrumentRepository.save(newInstrument);
      } else {
        // 4. 존재하는 악기는 숙련도 업데이트 처리 (옵션)
        existingInstruments.stream()
            .filter(inst -> inst.getInstrument() == dto.type())
            .findFirst()
            .ifPresent(inst -> inst.updateProficiency(dto.level()));
      }
    }

//     5. 변경된 악기 리스트를 UserEntity에 다시 세팅 (필요시)
     user.setInstruments(existingInstruments); // setter 있다면 호출
  }


}
