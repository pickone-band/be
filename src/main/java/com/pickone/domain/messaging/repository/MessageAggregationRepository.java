package com.pickone.domain.messaging.repository;

import com.pickone.domain.messaging.model.document.MessageDocument;

import java.util.List;

public interface MessageAggregationRepository {

  List<MessageDocument> findLatestMessagesPerRoom(List<Long> roomIds);
}
