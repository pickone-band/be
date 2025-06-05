package com.PickOne.global.verification.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserRegisteredEvent extends ApplicationEvent {

    private final Long userId;
    private final String email;

    public UserRegisteredEvent(Object source, Long userId, String email) {
        super(source);
        this.userId = userId;
        this.email = email;
    }
}
