// User.java
package com.PickOne.user.model.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "id")
public class User {
    private Long id;
    private Email email;
    private Password password;

    private User(Long id, Email email, Password password) {
        this.id = id;
        this.email = email;
        this.password = password;
    }

    public static User create(Email email, Password password) {
        return new User(null, email, password);
    }

    public static User of(Long id, Email email, Password password) {
        return new User(id, email, password);
    }

    public String getEmailValue() {
        return this.email.getValue();
    }

    public String getPasswordValue() {
        return this.password.getValue();
    }
}