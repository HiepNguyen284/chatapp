package com.group4.chatapp.dtos.chatbot;

import jakarta.validation.constraints.Size;

public record ChatbotConversationCreateDto(
    @Size(max = 120)
    String title
) {}
