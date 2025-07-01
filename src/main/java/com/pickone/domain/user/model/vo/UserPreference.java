package com.pickone.domain.user.model.vo;

import com.pickone.global.common.enums.Genre;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class UserPreference {
  @Enumerated(EnumType.STRING)
  private Genre primaryGenre;
  @Enumerated(EnumType.STRING)
  private Genre secondaryGenre;
  @Enumerated(EnumType.STRING)
  private Genre tertiaryGenre;

  public static UserPreference from(List<Genre> genres) {
    return new UserPreference(
        genres.size() > 0 ? genres.get(0) : null,
        genres.size() > 1 ? genres.get(1) : null,
        genres.size() > 2 ? genres.get(2) : null
    );
  }

  public List<Genre> asList() {
    List<Genre> result = new ArrayList<>();
    if (primaryGenre != null) result.add(primaryGenre);
    if (secondaryGenre != null) result.add(secondaryGenre);
    if (tertiaryGenre != null) result.add(tertiaryGenre);
    return result;
  }
}
