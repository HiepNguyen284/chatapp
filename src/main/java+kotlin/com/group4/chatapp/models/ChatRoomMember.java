package com.group4.chatapp.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Table(
    name = "chat_room_member_roles",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_chat_room_member_role_room_user", columnNames = {
            "chat_room_id",
            "user_id"
        })
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    @Builder.Default
    private boolean isAdmin = false;

    @CreationTimestamp
    @Column(name = "joined_at", nullable = false, updatable = false)
    private Timestamp joinedAt;

    public static ChatRoomMember createMember(ChatRoom chatRoom, User user, boolean isAdmin) {
        return ChatRoomMember.builder()
            .chatRoom(chatRoom)
            .user(user)
            .isAdmin(isAdmin)
            .build();
    }
}
