package com.group4.chatapp.dtos.chatbot;

import com.group4.chatapp.models.ChatbotMessage;

import java.sql.Timestamp;
import java.util.Locale;

public record ChatbotMessageDto(
    long id,
    String role,
    String content,
    Timestamp createdOn
) {

    public ChatbotMessageDto(ChatbotMessage message) {
        this(
            message.getId(),
            message.getRole().name().toLowerCase(Locale.ROOT),
            message.getContent(),
            message.getCreatedOn()
        );
    }
}
