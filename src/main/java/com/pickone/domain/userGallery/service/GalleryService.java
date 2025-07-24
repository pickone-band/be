package com.pickone.domain.userGallery.service;


import com.pickone.domain.user.entity.User;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.domain.userGallery.entity.GalleryItem;
import com.pickone.domain.userGallery.repository.GalleryItemRepository;
import com.pickone.global.exception.BusinessException;
import com.pickone.global.exception.ErrorCode;
import com.pickone.global.s3.service.S3Uploader;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class GalleryService {
    private final S3Uploader s3Uploader;
    private final GalleryItemRepository repo;
    private final UserJpaRepository userJpaRepository;
    @Transactional
    public GalleryItem upload(MultipartFile file, Long userId, String caption) throws IOException {
        // 1) S3에 업로드 (profiles가 아니라 gallery 디렉터리 사용)
        String key = s3Uploader.uploadKey(file, "gallery/" + userId);
        String publicUrl = s3Uploader.getPublicUrl(key);

        // 2) DB에 메타 저장
        GalleryItem item = new GalleryItem();
        item.setUserId(userId);
        item.setS3Key(key);
        item.setUrl(publicUrl);
        item.setCaption(caption);
        return repo.save(item);
    }

    @Transactional(readOnly = true)
    public List<GalleryItem> list(Long targetUserId) {
        User user = userJpaRepository.findById(targetUserId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));

        return repo.findAllByUserIdOrderByUploadedAtDesc(targetUserId);
    }

    @Transactional
    public void delete(Long itemId, Long userId) {
        GalleryItem item = repo.findById(itemId)
                .filter(i -> i.getUserId().equals(userId))
                .orElseThrow(() -> new IllegalArgumentException("삭제 권한이 없습니다."));
        // S3에서 삭제
        s3Uploader.delete(item.getS3Key());
        repo.delete(item);
    }

}