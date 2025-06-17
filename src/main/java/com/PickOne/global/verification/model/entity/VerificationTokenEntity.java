package com.PickOne.global.verification.model.entity;

import com.PickOne.domain.user.model.entity.UserEntity;
import com.PickOne.global.verification.model.domain.VerificationType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "verification_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VerificationTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false, unique = true)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerificationType type;

    @Column(nullable = false)
    private LocalDateTime expiredAt;

    @Column(nullable = false)
    private boolean isUsed;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiredAt);
    }

    public void markAsUsed() {
        this.isUsed = true;
    }

    // 외부는 반드시 builder 사용
    @Builder
    private VerificationTokenEntity(UserEntity user,
                                    String email,
                                    String token,
                                    VerificationType type,
                                    LocalDateTime expiredAt,
                                    boolean isUsed) {
        this.user = user;
        this.email = email;
        this.token = token;
        this.type = type;
        this.expiredAt = expiredAt;
        this.isUsed = isUsed;
    }
}
