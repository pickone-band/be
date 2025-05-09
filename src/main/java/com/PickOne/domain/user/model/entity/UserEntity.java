// UserEntity.java
package com.PickOne.domain.user.model.entity;

import com.PickOne.global.common.entity.BaseEntity;
import com.PickOne.domain.user.model.domain.Email;
import com.PickOne.domain.user.model.domain.Password;
import com.PickOne.domain.user.model.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean enabled = true;

    // 패키지 프라이빗 세터 - 같은 패키지의 테스트에서만 접근 가능
    void setEmail(String email) {
        this.email = email;
    }

    void setPassword(String password) {
        this.password = password;
    }

    void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    void setId(Long id) {
        this.id = id;
    }

    public static UserEntity from(User user) {
        UserEntity entity = new UserEntity();
        entity.email = user.getEmailValue();
        entity.password = user.getPasswordValue();
        return entity;
    }

    public User toDomain() {
        return User.of(
                this.id,
                Email.of(this.email),
                Password.ofEncoded(this.password)
        );
    }
}