package com.pickone.domain.messaging.repository.impl;

import com.pickone.domain.messaging.model.entity.ChatRole;
import com.pickone.domain.messaging.model.entity.ChatRoomUserEntity;
import com.pickone.domain.messaging.model.entity.QChatRoomUserEntity;
import com.pickone.domain.messaging.repository.ChatRoomUserQueryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChatRoomUserQueryRepositoryImpl implements ChatRoomUserQueryRepository {

  private final JPAQueryFactory queryFactory;

  QChatRoomUserEntity cru = QChatRoomUserEntity.chatRoomUserEntity;

  @Override
  public Optional<ChatRoomUserEntity> findOwnerOfRoom(Long roomId) {
    return Optional.ofNullable(
        queryFactory.selectFrom(cru)
            .where(
                cru.chatRoom.id.eq(roomId),
                cru.role.eq(ChatRole.OWNER)
            )
            .fetchOne()
    );
  }

  @Override
  public List<Long> findUserIdsByRoomId(Long roomId) {
    return queryFactory.select(cru.user.id)
        .from(cru)
        .where(cru.chatRoom.id.eq(roomId))
        .fetch();
  }
}
