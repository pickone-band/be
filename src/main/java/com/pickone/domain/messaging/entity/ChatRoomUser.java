package com.pickone.domain.messaging.entity;

import com.pickone.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoomUser {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  private ChatRoom chatRoom;

  @ManyToOne(fetch = FetchType.LAZY)
  private User user;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ChatRole role;

  public static ChatRoomUser create(ChatRoom chatRoom, User user, ChatRole role) {
    return new ChatRoomUser(null, chatRoom, user, role);
  }

  public boolean isOwner() {
    return this.role == ChatRole.OWNER;
  }
}
