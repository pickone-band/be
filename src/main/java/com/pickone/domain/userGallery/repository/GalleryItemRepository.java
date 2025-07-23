package com.pickone.domain.userGallery.repository;

import com.pickone.domain.userGallery.entity.GalleryItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GalleryItemRepository extends JpaRepository<GalleryItem, Long> {
    List<GalleryItem> findAllByUserIdOrderByUploadedAtDesc(Long userId);
}
