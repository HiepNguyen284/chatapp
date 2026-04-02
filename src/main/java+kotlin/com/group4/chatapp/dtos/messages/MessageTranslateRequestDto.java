package com.group4.chatapp.dtos.messages;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record MessageTranslateRequestDto(
    @NotBlank
    @Size(max = 2000)
    String text,

    @NotBlank
    @Size(max = 16)
    String targetLanguage,

    @Size(max = 16)
    String sourceLanguage,

    @Size(max = 8)
    List<@Size(max = 500) String> previousMessages
) {}
