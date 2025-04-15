package com.PickOne.user.model.entity;

import com.PickOne.common.entity.BaseEntity;
import com.PickOne.user.model.domain.user.Email;
import com.PickOne.user.model.domain.user.Password;
import com.PickOne.user.model.domain.user.User;
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
        return User.of(this.id,
                Email.of(this.email),
                Password.ofEncoded(this.password)
        );
    }

    // 생성자
    private UserEntity(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // 비밀번호 변경 메서드
    public UserEntity withPassword(String newPassword) {
        UserEntity entity = new UserEntity(this.email, newPassword);
        entity.id = this.id;
        return entity;
    }
}