package com.pickone.domain.messaging.repository.impl;

import com.pickone.domain.messaging.entity.ChatRole;
import com.pickone.domain.messaging.entity.ChatRoomUser;
import com.pickone.domain.messaging.entity.QChatRoomUser;
import com.pickone.domain.messaging.repository.ChatRoomUserQueryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChatRoomUserQueryRepositoryImpl implements ChatRoomUserQueryRepository {

  private final JPAQueryFactory queryFactory;

  QChatRoomUser cru = QChatRoomUser.chatRoomUser;

  @Override
  public Optional<ChatRoomUser> findOwnerOfRoom(Long roomId) {
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
