package com.PickOne.domain.user.model.entity;

import com.PickOne.domain.user.model.domain.*;
import com.PickOne.global.exception.BusinessException;
import com.PickOne.global.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "password", nullable = false))
    private Password password;

    @Column(name = "nickname", nullable = false, unique = true)
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

    @Column(nullable = false)
    private boolean isPublic;

    @Column(nullable = false)
    private boolean isVerified;

    @Column(nullable = false)
    private boolean isOauth;

        @ElementCollection(fetch = FetchType.LAZY)
        @CollectionTable(name = "user_instruments", joinColumns = @JoinColumn(name = "user_id"))
        private List<Instrument> instruments;

        @ElementCollection(fetch = FetchType.LAZY)
        @CollectionTable(name = "user_genres", joinColumns = @JoinColumn(name = "user_id"))
        private List<Genre> genres;

    public UserEntity(
            String email,
            Password password,
            String nickname,
            String profileImage,
            Role role,
            boolean isPublic,
            boolean isOauth,
            List<Instrument> instruments,
            List<Genre> genres,
            Gender gender,
            LocalDate birthDate
    ) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.role = role;
        this.isPublic = isPublic;
        this.isOauth = isOauth;
        this.isVerified = false;
        this.instruments = instruments;
        this.genres = genres;
        this.gender = gender;
        this.birthDate = birthDate;
    }

    public void verify(){
        if(isVerified){
            throw new BusinessException(ErrorCode.ALREADY_VERIFIED);
        }
        isVerified = true;
    }
    public void updateFromDomain(User user) {
        this.email = user.getEmail().getValue();
        this.password = user.getPassword();
        this.nickname = user.getNickname().getValue();
        this.profileImage = user.getProfileImage().getUrl();
        this.isPublic = user.isPublic();
        this.isVerified = user.isVerified();
        this.isOauth = user.isOauth();
        this.role = user.getRole();
        this.instruments = user.getInstruments();
        this.genres = user.getGenres();
        this.gender = user.getGender();
        this.birthDate = user.getBirthDate();
    }
}