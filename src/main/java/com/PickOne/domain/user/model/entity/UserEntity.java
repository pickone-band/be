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