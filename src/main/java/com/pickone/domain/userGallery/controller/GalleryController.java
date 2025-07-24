package com.pickone.domain.userGallery.controller;

import com.pickone.domain.userGallery.dto.GalleryItemResponseDto;
import com.pickone.domain.userGallery.entity.GalleryItem;
import com.pickone.domain.userGallery.service.GalleryService;
import com.pickone.global.exception.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Gallery", description = "프로필 갤러리 관리")
@RestController
@RequestMapping("/api/users/gallery")
@RequiredArgsConstructor
public class GalleryController {

    private final GalleryService galleryService;

    @Operation(summary = "갤러리 이미지 업로드", description = "사용자의 프로필 갤러리 이미지를 S3에 업로드하고, 메타데이터를 저장합니다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse<GalleryItemResponseDto>> upload(
            @Parameter(hidden = true)
            @AuthenticationPrincipal(expression = "id") Long userId,
            @RequestPart("file") MultipartFile file,
            @RequestParam(value = "caption", required = false) String caption
    ) throws IOException {
        GalleryItem item = galleryService.upload(file, userId, caption);
        GalleryItemResponseDto dto = GalleryItemResponseDto.from(item);
        return BaseResponse.success(dto);
    }

    @Operation(summary = "갤러리 목록 조회", description = "현재 로그인된 사용자의 갤러리 이미지 목록을 조회합니다.")
    @GetMapping("/{targetUserId}")
    public ResponseEntity<BaseResponse<List<GalleryItemResponseDto>>> list(
            @Parameter(hidden = true)
            @PathVariable("targetUserId") Long targetUserId
    ) {
        List<GalleryItemResponseDto> list = galleryService.list(targetUserId).stream()
                .map(GalleryItemResponseDto::from)
                .collect(Collectors.toList());
        return BaseResponse.success(list);
    }

    @Operation(summary = "갤러리 이미지 삭제", description = "지정한 갤러리 이미지를 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Void>> delete(
            @Parameter(hidden = true)
            @AuthenticationPrincipal(expression = "id") Long userId,
            @PathVariable("id") Long itemId
    ) {
        galleryService.delete(itemId, userId);
        return BaseResponse.success();
    }
}