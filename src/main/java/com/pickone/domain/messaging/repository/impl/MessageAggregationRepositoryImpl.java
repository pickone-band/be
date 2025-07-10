package com.pickone.domain.messaging.repository.impl;

import com.pickone.domain.messaging.document.Message;
import com.pickone.domain.messaging.repository.MessageAggregationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@RequiredArgsConstructor
@Repository
public class MessageAggregationRepositoryImpl implements MessageAggregationRepository {

  private final MongoTemplate mongoTemplate;

  @Override
  public List<Message> findLatestMessagesPerRoom(List<Long> roomIds) {
    Aggregation aggregation = Aggregation.newAggregation(
        match(Criteria.where("roomId").in(roomIds)),
        sort(Sort.Direction.DESC, "sentAt"),
        group("roomId").first("roomId").as("roomId")
            .first("senderId").as("senderId")
            .first("content").as("content")
            .first("sentAt").as("sentAt"),
        sort(Sort.Direction.DESC, "sentAt")
    );

    return mongoTemplate.aggregate(aggregation, "messages", Message.class)
        .getMappedResults();
  }

  @Override
  public List<Message> findAllByRoomId(Long roomId) {
    Query query = new Query(Criteria.where("roomId").is(roomId));
    return mongoTemplate.find(query, Message.class);
  }
}

