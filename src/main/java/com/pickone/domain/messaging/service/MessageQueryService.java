package com.pickone.domain.messaging.service;

import com.pickone.domain.messaging.dto.MessageResponse;
import com.pickone.domain.messaging.mapper.MessageMapper;
import com.pickone.domain.messaging.repository.MessageAggregationRepository;
import com.pickone.domain.messaging.repository.MessageReadStatusMongoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class MessageQueryService {

  private final MessageAggregationRepository messageAggregationRepository;
  private final MessageReadStatusMongoRepository readStatusRepository;
  private final MessageMapper messageMapper;

  public List<MessageResponse> getMessages(Long roomId) {
    log.info("[MessageQueryService] 메시지 목록 조회 - roomId: {}", roomId);
    return messageMapper.toResponseList(
        messageAggregationRepository.findAllByRoomId(roomId)
    );
  }

  public long countUnread(Long roomId, Long userId) {
    long count = readStatusRepository.countByRoomIdAndUserIdAndIsReadFalse(roomId, userId);
    log.info("[MessageQueryService] 안 읽은 메시지 개수 - roomId: {}, userId: {}, count: {}", roomId, userId, count);
    return count;
  }
}