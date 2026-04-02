package com.group4.chatapp.dtos.chatbot;

public record ChatbotStreamDoneDto(
    long conversationId,
    long assistantMessageId,
    String content
) {}
