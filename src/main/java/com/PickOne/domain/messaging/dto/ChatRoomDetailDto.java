package com.PickOne.domain.messaging.dto;

import java.util.List;

public record ChatRoomDetailDto(
        Long roomId,
        String name,
        List<String> participantNicknames
) {}