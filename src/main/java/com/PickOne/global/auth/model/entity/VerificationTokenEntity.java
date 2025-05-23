package com.PickOne.global.auth.model.entity;

import com.PickOne.global.auth.model.domain.VerificationToken;
import com.PickOne.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "verification_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VerificationTokenEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String token;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "token_type", nullable = false)
    private VerificationToken.TokenType tokenType;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    // 도메인 모델로 변환
    public VerificationToken toDomain() {
        return new VerificationToken(token, userId, tokenType, getCreatedAt(), expiryDate);
    }

    // 도메인 모델에서 엔티티 생성
    public static VerificationTokenEntity fromDomain(VerificationToken token) {
        VerificationTokenEntity entity = new VerificationTokenEntity();
        entity.token = token.getToken();
        entity.userId = token.getUserId();
        entity.tokenType = token.getTokenType();
        entity.expiryDate = token.getExpiryDate();
        return entity;
    }
}