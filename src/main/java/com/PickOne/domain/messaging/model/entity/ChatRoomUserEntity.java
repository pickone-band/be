    package com.PickOne.domain.messaging.model.entity;

    import com.PickOne.domain.user.model.entity.UserEntity;
    import jakarta.persistence.*;
    import lombok.AccessLevel;
    import lombok.AllArgsConstructor;
    import lombok.Getter;
    import lombok.NoArgsConstructor;

    @Entity
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public class ChatRoomUserEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        private ChatRoomEntity chatRoom;

        @ManyToOne(fetch = FetchType.LAZY)
        private UserEntity user;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private ChatRole role;

        public ChatRoomUserEntity(ChatRoomEntity chatRoom, UserEntity user, ChatRole role) {
            this.chatRoom = chatRoom;
            this.user = user;
            this.role = role;
        }

        public boolean isAdminOrOwner() {
            return role == ChatRole.OWNER || role == ChatRole.ADMIN;
        }

        public boolean isOwner() {
            return role == ChatRole.OWNER;
        }
    }