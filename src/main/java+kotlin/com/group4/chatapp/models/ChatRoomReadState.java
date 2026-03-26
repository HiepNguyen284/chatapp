package com.group4.chatapp.models;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Table(
    name = "chat_room_read_states",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"room_id", "reader_id"})
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomReadState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private ChatRoom room;

    @ManyToOne(optional = false)
    @JoinColumn(name = "reader_id")
    private User reader;

    @Column(nullable = false)
    private Timestamp lastReadAt;
}
