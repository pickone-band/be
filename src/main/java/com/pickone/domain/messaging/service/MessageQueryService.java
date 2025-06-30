package com.pickone.domain.messaging.service;

import com.pickone.domain.messaging.dto.MessageDto;
import com.pickone.domain.messaging.repository.MessageAggregationRepository;
import com.pickone.domain.messaging.repository.MessageReadStatusMongoRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MessageQueryService {
  private final MessageAggregationRepository messageAggregationRepository;
  private final MessageReadStatusMongoRepository readStatusRepository;

  public List<MessageDto> getMessages(Long roomId) {
    return messageAggregationRepository.findAllByRoomId(roomId)
        .stream()
        .map(MessageDto::of)
        .collect(Collectors.toList());
  }

  public long countUnread(Long roomId, Long userId) {
    return readStatusRepository.countByRoomIdAndUserIdAndIsReadFalse(roomId, userId);
  }
}