package com.PickOne.global.oauth2.model.entity;

import com.PickOne.domain.user.model.entity.UserEntity;
import com.PickOne.global.common.entity.BaseEntity;
import com.PickOne.global.oauth2.model.domain.OAuth2Provider;
import com.PickOne.global.oauth2.model.domain.UserConnection;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_connections",
        uniqueConstraints = @UniqueConstraint(columnNames = {"provider", "provider_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserConnectionEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "provider_id", nullable = false)
    private String providerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OAuth2Provider provider;

    @Column
    private String email;

    @Column(name = "name")
    private String name;

    @Column(name = "last_login", nullable = false)
    private LocalDateTime lastLogin;

    /**
     * 도메인 객체로 변환
     */
    public UserConnection toDomain() {
        return UserConnection.of(
                id, user.getId(), providerId, provider, email, name, lastLogin
        );
    }

    /**
     * 연결 정보 업데이트
     */
    public void updateConnectionInfo(String email, String name) {
        this.email = email;
        this.name = name;
        this.lastLogin = LocalDateTime.now();
    }

    /**
     * 도메인 객체로부터 엔티티 생성
     */
    public static UserConnectionEntity from(UserConnection connection, UserEntity user) {
        UserConnectionEntity entity = new UserConnectionEntity();
        entity.user = user;
        entity.providerId = connection.getProviderId();
        entity.provider = connection.getProvider();
        entity.email = connection.getEmailValue();
        entity.name = connection.getName();
        entity.lastLogin = connection.getLastLogin();
        return entity;
    }

    /**
     * 신규 사용자 연결 생성
     */
    public static UserConnectionEntity create(UserEntity user, String providerId,
                                              OAuth2Provider provider, String email, String name) {
        UserConnectionEntity entity = new UserConnectionEntity();
        entity.user = user;
        entity.providerId = providerId;
        entity.provider = provider;
        entity.email = email;
        entity.name = name;
        entity.lastLogin = LocalDateTime.now();
        return entity;
    }
}