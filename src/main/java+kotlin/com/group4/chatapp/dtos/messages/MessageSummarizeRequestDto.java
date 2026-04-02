package com.group4.chatapp.dtos.messages;

import jakarta.validation.constraints.Size;

import java.util.List;

public record MessageSummarizeRequestDto(
    @Size(min = 1, max = 40)
    List<@Size(max = 500) String> messages,

    @Size(max = 120)
    String roomName
) {}
