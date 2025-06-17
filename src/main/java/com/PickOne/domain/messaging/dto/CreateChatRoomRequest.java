package com.PickOne.domain.messaging.dto;

import java.util.List;

public record CreateChatRoomRequest(
        String name,
        List<Long> participantIds
) {}
