package com.group4.chatapp.dtos.messages;

public record MessageTypingEventDto(
    long roomId,
    String sender,
    boolean typing
) {}
