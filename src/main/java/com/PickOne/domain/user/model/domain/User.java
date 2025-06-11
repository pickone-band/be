package com.PickOne.domain.user.model.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public class User {

    private final Long id;
    private final Email email;
    private final Password password;
    private final Nickname nickname;
    private final Gender gender;
    private final LocalDate birthDate;
    private final ProfileImage profileImage;
    private final boolean isPublic;
    private final boolean isVerified;
    private final boolean isOauth;
    private final Role role;
    private final List<Instrument> instruments;
    private final List<Genre> genres;

    public User verify() {
        if (this.isVerified) throw new IllegalStateException("이미 인증된 사용자입니다.");
        return new User(id, email, password, nickname, gender, birthDate, profileImage, isPublic, true, isOauth, role, instruments, genres);
    }

    public User changePassword(Password newPassword) {
        return new User(id, email, newPassword, nickname, gender, birthDate, profileImage, isPublic, isVerified, isOauth, role, instruments, genres);
    }

    public User changeNickname(Nickname newNickname) {
        return new User(id, email, password, newNickname, gender, birthDate, profileImage, isPublic, isVerified, isOauth, role, instruments, genres);
    }

    public User changeProfileImage(ProfileImage newImage) {
        return new User(id, email, password, nickname, gender, birthDate, newImage, isPublic, isVerified, isOauth, role, instruments, genres);
    }

    public User changeInstruments(List<Instrument> newInstruments) {
        return new User(id, email, password, nickname, gender, birthDate, profileImage, isPublic, isVerified, isOauth, role, List.copyOf(newInstruments), genres);
    }

    public User changeGenres(List<Genre> newGenres) {
        return new User(id, email, password, nickname, gender, birthDate, profileImage, isPublic, isVerified, isOauth, role, instruments, List.copyOf(newGenres));
    }

    public User addInstrument(Instrument newInstrument) {
        if (this.instruments.contains(newInstrument)) return this;
        List<Instrument> updated = new java.util.ArrayList<>(this.instruments);
        updated.add(newInstrument);
        return new User(id, email, password, nickname, gender, birthDate, profileImage, isPublic, isVerified, isOauth, role, List.copyOf(updated), genres);
    }

    public User removeInstrument(Instrument instrument) {
        List<Instrument> updated = this.instruments.stream()
                .filter(i -> !i.equals(instrument))
                .toList();
        return new User(id, email, password, nickname, gender, birthDate, profileImage, isPublic, isVerified, isOauth, role, updated, genres);
    }

    public User addGenre(Genre newGenre) {
        if (this.genres.contains(newGenre)) return this;
        List<Genre> updated = new java.util.ArrayList<>(this.genres);
        updated.add(newGenre);
        return new User(id, email, password, nickname, gender, birthDate, profileImage, isPublic, isVerified, isOauth, role, instruments, List.copyOf(updated));
    }

    public User removeGenre(Genre genre) {
        List<Genre> updated = this.genres.stream()
                .filter(g -> !g.equals(genre))
                .toList();
        return new User(id, email, password, nickname, gender, birthDate, profileImage, isPublic, isVerified, isOauth, role, instruments, updated);
    }


    public User updateWith(User updateData) {
        return new User(
                this.id,
                Optional.ofNullable(updateData.getEmail()).orElse(this.email),
                Optional.ofNullable(updateData.getPassword()).orElse(this.password),
                Optional.ofNullable(updateData.getNickname()).orElse(this.nickname),
                Optional.ofNullable(updateData.getGender()).orElse(this.gender),
                Optional.ofNullable(updateData.getBirthDate()).orElse(this.birthDate),
                Optional.ofNullable(updateData.getProfileImage()).orElse(this.profileImage),
                updateData.isPublic(),
                this.isVerified,
                this.isOauth,
                this.role,
                Optional.ofNullable(updateData.getInstruments()).orElse(this.instruments),
                Optional.ofNullable(updateData.getGenres()).orElse(this.genres)
        );
    }
}
