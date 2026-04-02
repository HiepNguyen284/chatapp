package com.group4.chatapp.dtos.chatbot;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChatbotStreamRequestDto(
    @NotBlank
    @Size(max = 4000)
    String message,

    @Size(max = 80)
    String model,

    Boolean useMcp,

    @Size(max = 120)
    String mcpSessionId,

    @Size(max = 2000)
    String mcpMetadata
) {}
