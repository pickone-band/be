  package com.pickone.domain.user.entity;

  import com.pickone.domain.user.model.domain.Gender;
  import com.pickone.domain.user.model.domain.Role;
  import com.pickone.domain.user.model.vo.*;
  import com.pickone.domain.userInstrument.entity.UserInstrument;
  import com.pickone.domain.userMusic.UserMusic;
  import com.pickone.global.common.entity.BaseEntity;
  import com.pickone.global.common.enums.Genre;
  import com.pickone.global.common.enums.Mbti;
  import jakarta.persistence.*;
  import lombok.*;

  import java.time.LocalDate;
  import java.util.ArrayList;
  import java.util.List;

  @Entity
  @Getter
  @NoArgsConstructor(access = AccessLevel.PROTECTED)
  @AllArgsConstructor(access = AccessLevel.PROTECTED)
  @Table(name = "users")
  public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private UserProfile profile;

    @Embedded
    private UserStatus status;

    @Embedded
    private UserPreference preference;

    @Embedded
    private UserAuthInfo authInfo;

    @Embedded
    private SecurityInfo securityInfo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserInstrument> instruments = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserMusic> musicList = new ArrayList<>();

    public static User create(
        String email, String password, String nickname,
        Gender gender, LocalDate birthDate, Mbti mbti, List<?> genres, String profileImageUrl, String introduction
    ) {
      return new User(
          null,
          UserProfile.of(nickname, email, birthDate, gender, mbti, profileImageUrl, introduction),
          UserStatus.init(),
          UserPreference.from((List) genres),
          UserAuthInfo.of(password, null, null),
          SecurityInfo.of(false, null),
          Role.USER,
          new ArrayList<>(),
          new ArrayList<>()
      );
    }

    public void updateProfile(String nickname, LocalDate birthDate, Gender gender, Mbti mbti, String profileImageUrl, String introduction) {
      this.profile = UserProfile.of(nickname, this.profile.getEmail(), birthDate, gender, mbti, profileImageUrl, introduction);
    }

    public void changePassword(String newPassword) {
      this.authInfo = UserAuthInfo.of(newPassword, this.authInfo.getProvider(), this.authInfo.getProviderId());
    }

    public void verify() {
      this.status = this.status.verify();
    }

    public void lock() {
      this.status = this.status.lock();
    }

    public void setInstruments(List<UserInstrument> instrumentEntities) {
      this.instruments.clear();
      this.instruments.addAll(instrumentEntities);
      for (UserInstrument i : instrumentEntities) {
        i.setUser(this);
      }
    }

    public void updatePreference(List<Genre> genres) {
      this.preference = UserPreference.from(genres);
    }
  }
