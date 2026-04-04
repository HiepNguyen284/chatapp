package com.group4.chatapp.dtos.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record MessageTranslationDto(
    String translatedText,
    String detectedSourceLanguage,
    String targetLanguage
) {
    @JsonCreator
    public MessageTranslationDto(
        @JsonProperty("translatedText") String translatedText,
        @JsonProperty("detectedSourceLanguage") String detectedSourceLanguage,
        @JsonProperty("targetLanguage") String targetLanguage
    ) {
        this.translatedText = translatedText;
        this.detectedSourceLanguage = detectedSourceLanguage;
        this.targetLanguage = targetLanguage;
    }
}
