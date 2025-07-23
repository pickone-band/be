package com.pickone.domain.userGallery.dto;

import com.pickone.domain.userGallery.entity.GalleryItem;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "갤러리 아이템 응답 DTO")
public record GalleryItemResponseDto(

        @Schema(description = "갤러리 항목 식별자", example = "123")
        Long id,

        @Schema(description = "S3에 저장된 이미지 URL",
                example = "https://your-bucket.s3.amazonaws.com/gallery/1/uuid-filename.jpg")
        String url,

        @Schema(description = "이미지 설명(캡션)", example = "여름 바다 사진")
        String caption,

        @Schema(description = "업로드된 시각(ISO 8601)",
                example = "2025-07-15T12:34:56")
        LocalDateTime uploadedAt

) {
    public static GalleryItemResponseDto from(GalleryItem item) {
        return new GalleryItemResponseDto(
                item.getId(),
                item.getUrl(),
                item.getCaption(),
                item.getUploadedAt()
        );
    }
}
