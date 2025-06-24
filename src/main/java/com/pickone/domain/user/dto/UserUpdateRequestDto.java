package com.pickone.domain.user.dto;

import com.pickone.domain.user.model.entity.UserInstrumentEntity;
import com.pickone.global.common.enums.Instrument;
import com.pickone.global.common.enums.Proficiency;
import com.pickone.global.common.enums.Genre;
import com.pickone.global.common.enums.Mbti;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateRequestDto {

  @Size(min = 2, max = 20)
  private String nickname;
  private String profileImageUrl;
  private Boolean isPublic;
  private Mbti mbti;
  private List<Genre> genres;
  private List<UserInstrumentInfo> instruments;

  public List<UserInstrumentEntity> toInstrumentEntities() {
      if (instruments == null) {
          return List.of();
      }
    return instruments.stream().map(UserInstrumentInfo::toEntity).toList();
  }

  public List<Genre> toGenres() {
    return genres != null ? new ArrayList<>(genres) : List.of();
  }

  public record UserInstrumentInfo(Instrument instrument, Proficiency proficiency) {

    public static UserInstrumentInfo from(UserInstrumentEntity entity) {
      return new UserInstrumentInfo(entity.getInstrument(), entity.getProficiency());
    }

    public UserInstrumentEntity toEntity() {
      return UserInstrumentEntity.builder()
          .instrument(instrument)
          .proficiency(proficiency)
          .build();
    }
  }
}
