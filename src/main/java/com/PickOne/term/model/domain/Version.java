package com.PickOne.term.model.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.gradle.util.internal.VersionNumber;

import java.util.regex.Pattern;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class Version {
    private static final int MAX_LENGTH = 20;
    private static final Pattern VERSION_PATTERN = Pattern.compile("^\\d+(\\.\\d+)*$");

    private String value;
    private VersionNumber versionNumber;

    private Version(String value) {
        this.value = value;
        this.versionNumber = VersionNumber.parse(value);
    }

    public static Version of(String value) {
        validate(value);
        return new Version(value);
    }

    private static void validate(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("버전은 비어있을 수 없습니다.");
        }

        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("버전은 %d자를 초과할 수 없습니다.", MAX_LENGTH));
        }

        if (!VERSION_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("유효하지 않은 버전 형식입니다: " + value);
        }


        try {
            VersionNumber.parse(value);
        } catch (Exception e) {
            throw new IllegalArgumentException("유효하지 않은 버전 형식입니다: " + value, e);
        }
    }

    /**
     * 현재 버전이 다른 버전보다 최신인지 비교합니다.
     * 시맨틱 버저닝 규칙에 따라 정확하게 비교합니다.
     *
     * @param other 비교할 다른 버전
     * @return 현재 버전이 다른 버전보다 최신이면 true, 아니면 false
     */
    public boolean isNewerThan(Version other) {
        return this.versionNumber.compareTo(other.versionNumber) > 0;
    }

    /**
     * 현재 버전이 다른 버전과 동일한지 비교합니다.
     *
     * @param other 비교할 다른 버전
     * @return 현재 버전이 다른 버전과 동일하면 true, 아니면 false
     */
    public boolean isSameAs(Version other) {
        return this.versionNumber.compareTo(other.versionNumber) == 0;
    }

    /**
     * 현재 버전이 다른 버전보다 이전 버전인지 비교합니다.
     *
     * @param other 비교할 다른 버전
     * @return 현재 버전이 다른 버전보다 이전 버전이면 true, 아니면 false
     */
    public boolean isOlderThan(Version other) {
        return this.versionNumber.compareTo(other.versionNumber) < 0;
    }

    /**
     * 메이저 버전만 비교합니다.
     *
     * @param other 비교할 다른 버전
     * @return 현재 버전의 메이저 버전이 더 크면 true, 아니면 false
     */
    public boolean hasMajorUpdate(Version other) {
        return this.versionNumber.getMajor() > other.versionNumber.getMajor();
    }

    /**
     * 마이너 버전만 비교합니다. 메이저 버전이 같은 경우에만 의미가 있습니다.
     *
     * @param other 비교할 다른 버전
     * @return 메이저 버전이 같고 현재 버전의 마이너 버전이 더 크면 true, 아니면 false
     */
    public boolean hasMinorUpdate(Version other) {
        return this.versionNumber.getMajor() == other.versionNumber.getMajor()
                && this.versionNumber.getMinor() > other.versionNumber.getMinor();
    }

    /**
     * 패치 버전만 비교합니다. 메이저와 마이너 버전이 같은 경우에만 의미가 있습니다.
     *
     * @param other 비교할 다른 버전
     * @return 메이저와 마이너 버전이 같고 현재 버전의 패치 버전이 더 크면 true, 아니면 false
     */
    public boolean hasPatchUpdate(Version other) {
        return this.versionNumber.getMajor() == other.versionNumber.getMajor()
                && this.versionNumber.getMinor() == other.versionNumber.getMinor()
                && this.versionNumber.getMicro() > other.versionNumber.getMicro();
    }
}