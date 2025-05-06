package com.PickOne.global.security.model.domain;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public enum PermissionCode {

    POST_CREATE(Category.POST, "게시글 생성"),
    POST_READ(Category.POST, "게시글 조회"),
    POST_UPDATE(Category.POST, "게시글 수정"),
    POST_DELETE(Category.POST, "게시글 삭제");

    private final Category category;
    private final String description;

    PermissionCode(Category category, String description) {
        this.category = category;
        this.description = description;
    }
    /**
     * 특정 카테고리에 속하는 모든 권한 코드 조회
     */
    public static List<PermissionCode> getByCategory(Category category) {
        return Arrays.stream(values())
                .filter(code -> code.getCategory() == category)
                .collect(Collectors.toList());
    }

}
