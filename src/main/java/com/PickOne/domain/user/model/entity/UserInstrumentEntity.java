package com.PickOne.domain.user.model.entity;

import com.PickOne.global.common.entity.BaseEntity;
import com.PickOne.global.common.enums.Instrument;
import com.PickOne.global.common.enums.Proficiency;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_instruments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserInstrumentEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Instrument instrument;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Proficiency proficiency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Builder
    public UserInstrumentEntity(Instrument instrument, Proficiency proficiency) {
        this.instrument = instrument;
        this.proficiency = proficiency;
    }

    public void setUser(UserEntity user) {
        this.user = user;
        if (!user.getUserInstruments().contains(this)) {
            user.getUserInstruments().add(this);
        }
    }

}
