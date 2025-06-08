package com.PickOne.domain.user.model.entity;

import com.PickOne.domain.user.model.domain.*;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "email", nullable = false, unique = true))
    private Email email;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "password", nullable = false))
    private Password password;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "nickname", nullable = false, unique = true))
    private Nickname nickname;

    @Embedded
    @AttributeOverride(name = "url", column = @Column(name = "profile_image"))
    private ProfileImage profileImage;

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
            Email email,
            Password password,
            Nickname nickname,
            ProfileImage profileImage,
            Role role,
            boolean isPublic,
            boolean isOauth,
            List<Instrument> instruments,
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
        this.instruments = instruments;
        this.genres = genres;
    }
}