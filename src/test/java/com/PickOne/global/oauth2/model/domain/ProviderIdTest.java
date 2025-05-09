package com.PickOne.global.oauth2.model.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ProviderIdTest {

    @Test
    @DisplayName("유효한 제공자 ID 생성")
    void of_ValidProviderId() {
        // when
        ProviderId providerId = ProviderId.of("12345");

        // then
        assertEquals("12345", providerId.getValue());
    }

    @Test
    @DisplayName("null 제공자 ID - 예외 발생")
    void of_NullProviderId() {
        // when, then
        assertThrows(IllegalArgumentException.class, () -> {
            ProviderId.of(null);
        });
    }

    @Test
    @DisplayName("빈 제공자 ID - 예외 발생")
    void of_EmptyProviderId() {
        // when, then
        assertThrows(IllegalArgumentException.class, () -> {
            ProviderId.of("");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            ProviderId.of("   ");
        });
    }

    @Test
    @DisplayName("equals와 hashCode 테스트")
    void equalsAndHashCode() {
        // given
        ProviderId id1 = ProviderId.of("12345");
        ProviderId id2 = ProviderId.of("12345");
        ProviderId id3 = ProviderId.of("67890");

        // when, then
        assertEquals(id1, id2);
        assertNotEquals(id1, id3);
        assertEquals(id1.hashCode(), id2.hashCode());
    }
}