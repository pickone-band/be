package com.PickOne.domain.user.dto;

import com.PickOne.domain.user.model.entity.UserInstrumentEntity;
import com.PickOne.global.common.enums.Instrument;
import com.PickOne.global.common.enums.Proficiency;

import java.util.List;

public record UserInstrumentDto(
        Instrument instrument,
        Proficiency proficiency
) {
    public static UserInstrumentDto from(UserInstrumentEntity entity) {
        return new UserInstrumentDto(entity.getInstrument(), entity.getProficiency());
    }

    public UserInstrumentEntity toEntity() {
        return UserInstrumentEntity.builder()
                .instrument(instrument)
                .proficiency(proficiency)
                .build();
    }
}