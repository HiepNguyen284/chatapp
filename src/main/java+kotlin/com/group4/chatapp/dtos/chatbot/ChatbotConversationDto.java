package com.group4.chatapp.dtos.chatbot;

import com.group4.chatapp.models.ChatbotConversation;
import org.jspecify.annotations.Nullable;

import java.sql.Timestamp;

public record ChatbotConversationDto(
    long id,
    String title,
    String preview,
    boolean mcpEnabled,
    @Nullable String mcpSessionId,
    Timestamp createdOn,
    Timestamp updatedOn
) {

    public ChatbotConversationDto(ChatbotConversation conversation, @Nullable String preview) {
        this(
            conversation.getId(),
            conversation.getTitle(),
            preview == null ? "" : preview,
            conversation.isMcpEnabled(),
            conversation.getMcpSessionId(),
            conversation.getCreatedOn(),
            conversation.getUpdatedOn()
        );
    }
}
