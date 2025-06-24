package com.pickone.domain.messaging.repository;

import com.pickone.domain.messaging.model.document.MessageDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Repository
@RequiredArgsConstructor
public class MessageAggregationRepositoryImpl implements MessageAggregationRepository {

  private final MongoTemplate mongoTemplate;

  @Override
  public List<MessageDocument> findLatestMessagesPerRoom(List<Long> roomIds) {
    MatchOperation match = match(Criteria.where("roomId").in(roomIds));
    Aggregation aggregation = newAggregation(
        match,
        sort(Sort.Direction.DESC, "sentAt"),
        group("roomId").first("$$ROOT").as("latestMessage"),
        replaceRoot("latestMessage")
    );

    AggregationResults<MessageDocument> results = mongoTemplate.aggregate(
        aggregation,
        "messages",
        MessageDocument.class
    );

    return results.getMappedResults();
  }
}
