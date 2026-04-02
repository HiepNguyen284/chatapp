package com.group4.chatapp.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.jspecify.annotations.Nullable;

import java.sql.Timestamp;

@Entity
@Table(name = "chatbot_conversations")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatbotConversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User owner;

    @Column(nullable = false)
    private String title;

    @Column(name = "model_name", nullable = false)
    private String modelName;

    @Column(name = "mcp_enabled", nullable = false)
    private boolean mcpEnabled;

    @Nullable
    @Column(name = "mcp_session_id")
    private String mcpSessionId;

    @Nullable
    @Column(name = "mcp_metadata", columnDefinition = "TEXT")
    private String mcpMetadata;

    @CreationTimestamp
    @Column(name = "created_on", updatable = false)
    private Timestamp createdOn;

    @UpdateTimestamp
    @Column(name = "updated_on")
    private Timestamp updatedOn;

    @PreUpdate
    @PrePersist
    private void validate() {
        if (title == null || title.isBlank()) {
            throw new IllegalStateException("Conversation title must not be blank");
        }
        if (modelName == null || modelName.isBlank()) {
            throw new IllegalStateException("Conversation model must not be blank");
        }
    }
}
