package com.group4.chatapp.dtos.messages;

public record MessageTranslationDto(
    String translatedText,
    String detectedSourceLanguage,
    String targetLanguage
) {}
