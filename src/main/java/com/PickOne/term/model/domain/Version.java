package com.PickOne.term.model.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.regex.Pattern;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class Version {
    private static final int MAX_LENGTH = 20;
    private static final Pattern VERSION_PATTERN = Pattern.compile("^\\d+(\\.\\d+)*$");

    private String value;
    private int[] versionParts;

    private Version(String value) {
        this.value = value;
        this.versionParts = parseVersion(value);
    }

    private int[] parseVersion(String version) {
        String[] parts = version.split("\\.");
        int[] versionParts = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            versionParts[i] = Integer.parseInt(parts[i]);
        }
        return versionParts;
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
    }

    public boolean isNewerThan(Version other) {
        int[] otherParts = other.versionParts;
        for (int i = 0; i < Math.min(this.versionParts.length, otherParts.length); i++) {
            if (this.versionParts[i] > otherParts[i]) return true;
            if (this.versionParts[i] < otherParts[i]) return false;
        }
        return this.versionParts.length > otherParts.length;
    }

    public boolean isSameAs(Version other) {
        if (this.versionParts.length != other.versionParts.length) return false;

        for (int i = 0; i < this.versionParts.length; i++) {
            if (this.versionParts[i] != other.versionParts[i]) return false;
        }
        return true;
    }

    public boolean isOlderThan(Version other) {
        int[] otherParts = other.versionParts;
        for (int i = 0; i < Math.min(this.versionParts.length, otherParts.length); i++) {
            if (this.versionParts[i] < otherParts[i]) return true;
            if (this.versionParts[i] > otherParts[i]) return false;
        }
        return this.versionParts.length < otherParts.length;
    }

    public boolean hasMajorUpdate(Version other) {
        return getMajor() > other.getMajor();
    }

    public boolean hasMinorUpdate(Version other) {
        return getMajor() == other.getMajor() && getMinor() > other.getMinor();
    }

    public boolean hasPatchUpdate(Version other) {
        return getMajor() == other.getMajor()
                && getMinor() == other.getMinor()
                && getMicro() > other.getMicro();
    }

    public int getMajor() {
        return versionParts.length > 0 ? versionParts[0] : 0;
    }

    public int getMinor() {
        return versionParts.length > 1 ? versionParts[1] : 0;
    }

    public int getMicro() {
        return versionParts.length > 2 ? versionParts[2] : 0;
    }
}