package com.pickone.domain.user.model.vo;

import com.pickone.global.common.enums.Genre;
import com.pickone.global.common.enums.Mbti;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserPreference {

  @ElementCollection(fetch = FetchType.LAZY)
  @CollectionTable(name = "user_genres", joinColumns = @JoinColumn(name = "user_id"))
  @Column(name = "genre")
  @Enumerated(EnumType.STRING)
  private List<Genre> genres = new ArrayList<>();

  public static UserPreference ofNullable(List<Genre> genres) {
    return new UserPreference(
        genres == null ? new ArrayList<>() : genres
    );
  }

  public UserPreference updateGenres(List<Genre> newGenres) {
    return new UserPreference(newGenres == null ? this.genres : newGenres);
  }
}

