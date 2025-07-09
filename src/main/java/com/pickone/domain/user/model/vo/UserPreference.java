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
  private Genre genre1;
  @Enumerated(EnumType.STRING)
  private Genre genre2;
  @Enumerated(EnumType.STRING)
  private Genre genre3;
  @Enumerated(EnumType.STRING)
  private Genre genre4;
  @Enumerated(EnumType.STRING)
  private Genre genre5;
  @Enumerated(EnumType.STRING)
  private Genre genre6;
  @Enumerated(EnumType.STRING)
  private Genre genre7;
  @Enumerated(EnumType.STRING)
  private Genre genre8;

  public static UserPreference from(List<Genre> genres) {
    if (genres == null) {
      genres = List.of(); // 빈 리스트 처리
    }
    return new UserPreference(
        genres.size() > 0 ? genres.get(0) : null,
        genres.size() > 1 ? genres.get(1) : null,
        genres.size() > 2 ? genres.get(2) : null,
        genres.size() > 3 ? genres.get(3) : null,
        genres.size() > 4 ? genres.get(4) : null,
        genres.size() > 5 ? genres.get(5) : null,
        genres.size() > 6 ? genres.get(6) : null,
        genres.size() > 7 ? genres.get(7) : null
    );
  }

  public List<Genre> asList() {
    List<Genre> result = new ArrayList<>();
    if (genre1 != null) result.add(genre1);
    if (genre2 != null) result.add(genre2);
    if (genre3 != null) result.add(genre3);
    if (genre4 != null) result.add(genre4);
    if (genre5 != null) result.add(genre5);
    if (genre6 != null) result.add(genre6);
    if (genre7 != null) result.add(genre7);
    if (genre8 != null) result.add(genre8);
    return result;
  }
}
