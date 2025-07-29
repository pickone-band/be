package com.pickone.domain.userPerformance.dto;

import java.time.LocalDate;
import java.util.List;

public record PerformanceRecordResponse(
        Long id,
        String title,
        LocalDate date,
        String teamName,
        List<SongDto> songs,
        String thumbnailUrl,
        String videoUrl,
        String description
) {}
