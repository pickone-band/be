package com.PickOne.global.auth.model.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailMessageTest {

    @Test
    void testCreateHtmlEmailMessage() {
        String to = "test@example.com";
        String subject = "Test Subject";
        String body = "<h1>Hello</h1>";
        boolean isHtml = true;

        EmailMessage email = EmailMessage.of(to, subject, body, isHtml);

        assertEquals(to, email.getTo());
        assertEquals(subject, email.getSubject());
        assertEquals(body, email.getBody());
        assertTrue(email.isHtml());
    }

    @Test
    void testEqualsAndHashCode() {
        EmailMessage email1 = EmailMessage.of("a@a.com", "subject", "body", false);
        EmailMessage email2 = EmailMessage.of("a@a.com", "subject", "body", false);

        assertEquals(email1, email2);
        assertEquals(email1.hashCode(), email2.hashCode());
    }
}
