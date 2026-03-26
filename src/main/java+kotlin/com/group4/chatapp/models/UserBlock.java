package com.group4.chatapp.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "user_blocks",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"blocker_id", "blocked_id"})
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserBlock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "blocker_id", nullable = false)
    private User blocker;

    @ManyToOne(optional = false)
    @JoinColumn(name = "blocked_id", nullable = false)
    private User blocked;
}
