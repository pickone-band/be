package com.pickone.domain.messaging.repository;

import com.pickone.domain.messaging.document.Message;

import java.util.List;

public interface MessageAggregationRepository {
  List<Message> findLatestMessagesPerRoom(List<Long> roomIds);

  List<Message> findAllByRoomId(Long roomId);
}
