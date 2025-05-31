package com.PickOne.domain.consent.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "consents")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ConsentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 내부 식별자 (DB용)

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long termsId;

    @Column(nullable = false)
    private boolean consented;

    @Column(nullable = false)
    private LocalDateTime consentDate;
}