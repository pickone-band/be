package com.PickOne.global.auth.model.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class EmailMessage {
    private String to;
    private String subject;
    private String body;
    private boolean isHtml;

    private EmailMessage(String to, String subject, String body, boolean isHtml) {
        this.to = to;
        this.subject = subject;
        this.body = body;
        this.isHtml = isHtml;
    }

    public static EmailMessage of(String to, String subject, String body, boolean isHtml) {
        return new EmailMessage(to, subject, body, isHtml);
    }
}