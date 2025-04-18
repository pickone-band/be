package com.PickOne.term.model.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class VersionTest {

    @Nested
    @DisplayName("Version 생성 테스트")
    class VersionCreationTest {

        @Test
        @DisplayName("유효한 버전으로 Version을 생성할 수 있다")
        void createVersionWithValidValue() {
            // given
            String validVersion = "1.0.0";

            // when
            Version version = Version.of(validVersion);

            // then
            assertThat(version).isNotNull();
            assertThat(version.getValue()).isEqualTo(validVersion);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {" ", "  "})
        @DisplayName("빈 문자열이나 공백으로 Version을 생성할 수 없다")
        void throwsExceptionWhenVersionIsEmptyOrBlank(String invalidVersion) {
            // then
            assertThatThrownBy(() -> Version.of(invalidVersion))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("버전은 비어있을 수 없습니다");
        }

        @Test
        @DisplayName("최대 길이를 초과하는 버전으로 Version을 생성할 수 없다")
        void throwsExceptionWhenVersionExceedsMaxLength() {
            // given
            String tooLongVersion = "1.0.0.".repeat(10);

            // then
            assertThatThrownBy(() -> Version.of(tooLongVersion))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("버전은 20자를 초과할 수 없습니다");
        }

        @ParameterizedTest
        @ValueSource(strings = {"invalid", "1.a.0", "version1"})
        @DisplayName("유효하지 않은 형식의 버전으로 Version을 생성할 수 없다")
        void throwsExceptionWhenVersionFormatIsInvalid(String invalidFormat) {
            // then
            assertThatThrownBy(() -> Version.of(invalidFormat))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("유효하지 않은 버전 형식입니다");
        }
    }

    @Nested
    @DisplayName("Version 비교 테스트")
    class VersionComparisonTest {

        @ParameterizedTest
        @CsvSource({
                "2.0.0, 1.0.0, true",
                "1.1.0, 1.0.0, true",
                "1.0.1, 1.0.0, true",
                "1.0.0, 1.0.0, false",
                "1.0.0, 2.0.0, false",
                "1.0.0, 1.1.0, false",
                "1.0.0, 1.0.1, false"
        })
        @DisplayName("isNewerThan 메서드가 버전을 올바르게 비교한다")
        void testIsNewerThan(String version1, String version2, boolean expected) {
            // given
            Version v1 = Version.of(version1);
            Version v2 = Version.of(version2);

            // then
            assertThat(v1.isNewerThan(v2)).isEqualTo(expected);
        }

        @ParameterizedTest
        @CsvSource({
                "1.0.0, 1.0.0, true",
                "2.0.0, 2.0.0, true",
                "1.1.0, 1.1.0, true",
                "1.0.0, 2.0.0, false",
                "1.0.0, 1.1.0, false",
                "1.0.0, 1.0.1, false"
        })
        @DisplayName("isSameAs 메서드가 버전을 올바르게 비교한다")
        void testIsSameAs(String version1, String version2, boolean expected) {
            // given
            Version v1 = Version.of(version1);
            Version v2 = Version.of(version2);

            // then
            assertThat(v1.isSameAs(v2)).isEqualTo(expected);
        }

        @ParameterizedTest
        @CsvSource({
                "1.0.0, 2.0.0, true",
                "1.0.0, 1.1.0, true",
                "1.0.0, 1.0.1, true",
                "1.0.0, 1.0.0, false",
                "2.0.0, 1.0.0, false",
                "1.1.0, 1.0.0, false",
                "1.0.1, 1.0.0, false"
        })
        @DisplayName("isOlderThan 메서드가 버전을 올바르게 비교한다")
        void testIsOlderThan(String version1, String version2, boolean expected) {
            // given
            Version v1 = Version.of(version1);
            Version v2 = Version.of(version2);

            // then
            assertThat(v1.isOlderThan(v2)).isEqualTo(expected);
        }

        @Test
        @DisplayName("시맨틱 버저닝 규칙에 따라 버전 비교가 올바르게 동작한다")
        void compareSemanticVersionsCorrectly() {
            // given
            Version v19 = Version.of("1.9.0");
            Version v110 = Version.of("1.10.0");

            // then
            assertThat(v110.isNewerThan(v19)).isTrue();
            assertThat(v19.isOlderThan(v110)).isTrue();
        }

        @Test
        @DisplayName("hasMajorUpdate 메서드가 메이저 버전 업데이트를 올바르게 감지한다")
        void testHasMajorUpdate() {
            // given
            Version v100 = Version.of("1.0.0");
            Version v200 = Version.of("2.0.0");
            Version v110 = Version.of("1.1.0");

            // then
            assertThat(v200.hasMajorUpdate(v100)).isTrue();
            assertThat(v100.hasMajorUpdate(v200)).isFalse();
            assertThat(v110.hasMajorUpdate(v100)).isFalse();
        }

        @Test
        @DisplayName("hasMinorUpdate 메서드가 마이너 버전 업데이트를 올바르게 감지한다")
        void testHasMinorUpdate() {
            // given
            Version v100 = Version.of("1.0.0");
            Version v110 = Version.of("1.1.0");
            Version v200 = Version.of("2.0.0");

            // then
            assertThat(v110.hasMinorUpdate(v100)).isTrue();
            assertThat(v100.hasMinorUpdate(v110)).isFalse();
            assertThat(v200.hasMinorUpdate(v100)).isFalse(); // 메이저 버전이 다름
        }

        @Test
        @DisplayName("hasPatchUpdate 메서드가 패치 버전 업데이트를 올바르게 감지한다")
        void testHasPatchUpdate() {
            // given
            Version v100 = Version.of("1.0.0");
            Version v101 = Version.of("1.0.1");
            Version v110 = Version.of("1.1.0");

            // then
            assertThat(v101.hasPatchUpdate(v100)).isTrue();
            assertThat(v100.hasPatchUpdate(v101)).isFalse();
            assertThat(v110.hasPatchUpdate(v100)).isFalse(); // 마이너 버전이 다름
        }
    }
}