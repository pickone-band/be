package com.PickOne.domain.user.model.entity;

import com.PickOne.domain.user.model.domain.*;
import com.PickOne.global.common.entity.BaseEntity;
import com.PickOne.global.common.enums.Genre;
import com.PickOne.global.common.enums.Mbti;
import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "profile_image")
    private String profileImage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(name = "is_public", nullable = false)
    private boolean isPublic;

    @Column(name = "is_verified", nullable = false)
    private boolean isVerified;

    @Column(name = "is_oauth", nullable = false)
    private boolean isOauth;

    @Enumerated(EnumType.STRING)
    @Column
    private Mbti mbti;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_genres", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "genre")
    @Enumerated(EnumType.STRING)
    private List<Genre> genres;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserInstrumentEntity> userInstruments = new ArrayList<>();

    @Builder
    public UserEntity(
            String email,
            String password,
            String nickname,
            String profileImage,
            Role role,
            boolean isPublic,
            boolean isOauth,
            Gender gender,
            LocalDate birthDate,
            Mbti mbti,
            List<Genre> genres
    ) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.role = role;
        this.isPublic = isPublic;
        this.isOauth = isOauth;
        this.isVerified = false;
        this.gender = gender;
        this.birthDate = birthDate;
        this.mbti = mbti;
        this.genres = genres;
    }

    public void verify() {
        if (this.isVerified) {
            throw new BusinessException(ErrorCode.ALREADY_VERIFIED);
        }
        this.isVerified = true;
    }

    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void updateNickname(String nickname) {
        if (nickname != null) this.nickname = nickname;
    }

    public void updateProfileImage(String profileImage) {
        if (profileImage != null) this.profileImage = profileImage;
    }

    public void updateVisibility(Boolean isPublic) {
        if (isPublic != null) this.isPublic = isPublic;
    }

    public void updateMbti(Mbti mbti) {
        if (mbti != null) this.mbti = mbti;
    }

    public void updateGenres(List<Genre> genres) {
        if (genres != null) {
            this.genres = List.copyOf(genres);  // 불변 리스트로 교체
        }
    }
    public void updateInstruments(List<UserInstrumentEntity> newInstruments) {
        if (newInstruments != null) {
            for (UserInstrumentEntity instrument : newInstruments) {
                instrument.setUser(this);
            }
            this.userInstruments = List.copyOf(newInstruments);  // 불변으로 교체
        }
    }
}