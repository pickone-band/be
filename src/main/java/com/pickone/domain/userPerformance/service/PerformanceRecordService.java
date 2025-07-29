package com.pickone.domain.userPerformance.service;

import com.pickone.domain.user.entity.User;
import com.pickone.domain.user.repository.UserJpaRepository;
import com.pickone.domain.userPerformance.dto.PerformanceRecordRequest;
import com.pickone.domain.userPerformance.dto.PerformanceRecordResponse;
import com.pickone.domain.userPerformance.entity.Song;
import com.pickone.domain.userPerformance.dto.SongDto;
import com.pickone.domain.userPerformance.entity.PerformanceRecord;
import com.pickone.domain.userPerformance.repository.PerformanceRecordRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PerformanceRecordService {
    private final PerformanceRecordRepository recordRepository;
    private final UserJpaRepository userJpaRepository;

    public void save(Long userId, PerformanceRecordRequest req) {
        User user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<Song> songs = req.songs().stream()
                .map(s -> new Song(s.artist(),s.title()))
                .toList();

        PerformanceRecord record = PerformanceRecord.builder()
        .title(req.title())
                .date(req.date())
                .teamName(req.teamName())
                .songs(songs)
                .thumbnailUrl(req.thumbnailUrl())
                .videoUrl(req.videoUrl())
                .description(req.description())
                .user(user)
                .build();

        recordRepository.save(record);
    }

    public List<PerformanceRecordResponse> findByUser(Long userId) {
        return recordRepository.findByUserId(userId).stream()
                .map(r -> new PerformanceRecordResponse(
                        r.getId(),
                        r.getTitle(),
                        r.getDate(),
                        r.getTeamName(),
                        r.getSongs().stream()
                                .map(s -> new SongDto(s.getTitle(), s.getArtist()))
                                .toList(),
                        r.getThumbnailUrl(),
                        r.getVideoUrl(),
                        r.getDescription()
                ))
                .toList();
    }
}