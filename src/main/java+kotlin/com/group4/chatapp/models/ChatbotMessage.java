package com.group4.chatapp.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.jspecify.annotations.Nullable;

import java.sql.Timestamp;

@Entity
@Table(name = "chatbot_messages")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatbotMessage {

    public enum Role {
        USER,
        ASSISTANT,
        SYSTEM,
        TOOL
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private ChatbotConversation conversation;

    @Column(nullable = false)
    private Role role;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Nullable
    @Column(name = "metadata_json", columnDefinition = "TEXT")
    private String metadataJson;

    @CreationTimestamp
    @Column(name = "created_on", updatable = false)
    private Timestamp createdOn;

    @PreUpdate
    @PrePersist
    private void validate() {
        if (content == null || content.isBlank()) {
            throw new IllegalStateException("Chatbot message content must not be blank");
        }
    }
}
