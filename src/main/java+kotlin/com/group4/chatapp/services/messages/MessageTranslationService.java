package com.group4.chatapp.services.messages;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group4.chatapp.dtos.messages.MessageSummarizeRequestDto;
import com.group4.chatapp.dtos.messages.MessageSummaryDto;
import com.group4.chatapp.dtos.messages.MessageTranslateRequestDto;
import com.group4.chatapp.dtos.messages.MessageTranslationDto;
import com.group4.chatapp.exceptions.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class MessageTranslationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageTranslationService.class);

    private static final int CACHE_MAX_SIZE = 2048;
    private static final int SUMMARY_CACHE_MAX_SIZE = 1024;
    private static final int CONTEXT_MESSAGE_LIMIT = 8;
    private static final int CONTEXT_TEXT_LIMIT = 500;
    private static final int SUMMARY_MESSAGE_LIMIT = 40;
    private static final int SUMMARY_TEXT_LIMIT = 500;

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .build();
    private static final String TRANSLATOR_SYSTEM_PROMPT = """
        You are a translation engine.
        Translate the user's message into natural Vietnamese.
        Preserve intent, tone, names, emoji, and formatting.
        If the message is already Vietnamese, keep it natural and concise.
        Return only translated Vietnamese text, without explanation or quotes.
        """;
    private static final String SUMMARY_SYSTEM_PROMPT = """
        You are a concise conversation summarizer.
        Summarize the recent chat in Vietnamese.
        Keep key facts, tasks, decisions, and follow-up actions.
        Use short clear paragraphs and bullet points when useful.
        Do not invent facts that are not in the messages.
        """;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, MessageTranslationDto> translationCache = Collections.synchronizedMap(
        new LinkedHashMap<>(CACHE_MAX_SIZE + 1, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, MessageTranslationDto> eldest) {
                return size() > CACHE_MAX_SIZE;
            }
        }
    );
    private final Map<String, MessageSummaryDto> summaryCache = Collections.synchronizedMap(
        new LinkedHashMap<>(SUMMARY_CACHE_MAX_SIZE + 1, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, MessageSummaryDto> eldest) {
                return size() > SUMMARY_CACHE_MAX_SIZE;
            }
        }
    );

    @Value("${translation.kilo.base-url:https://api.kilo.ai/api/gateway}")
    private String kiloBaseUrl;

    @Value("${translation.kilo.model:anthropic/claude-sonnet-4.5}")
    private String kiloModel;

    @Value("${translation.kilo.api-key:}")
    private String kiloApiKey;

    @Value("${translation.kilo.request-timeout-seconds:45}")
    private int kiloRequestTimeoutSeconds;

    public MessageTranslationDto translate(MessageTranslateRequestDto dto) {
        var text = normalizeText(dto.text());
        var sourceLanguage = normalizeSourceLanguage(dto.sourceLanguage());
        var targetLanguage = normalizeTargetLanguage(dto.targetLanguage());
        var previousMessages = normalizePreviousMessages(dto.previousMessages());
        var cacheKey = buildCacheKey(text, sourceLanguage, targetLanguage, previousMessages);

        var cached = translationCache.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        try {
            var request = HttpRequest.newBuilder(buildCompletionsUri())
                .timeout(resolveRequestTimeout())
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + requireApiKey())
                .POST(HttpRequest.BodyPublishers.ofString(
                    buildRequestBody(text, sourceLanguage, previousMessages),
                    StandardCharsets.UTF_8
                ))
                .build();

            var response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new ApiException(HttpStatus.BAD_GATEWAY, "Translation service request failed");
            }

            var translatedText = parseAssistantText(response.body());
            var detectedSourceLanguage = "auto".equals(sourceLanguage) ? "" : sourceLanguage;

            var result = new MessageTranslationDto(
                translatedText,
                detectedSourceLanguage,
                targetLanguage
            );

            translationCache.put(cacheKey, result);
            return result;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return fallbackTranslation(text, sourceLanguage, targetLanguage, cacheKey, e);
        } catch (ApiException e) {
            if (!isUpstreamFailure(e)) {
                throw e;
            }

            return fallbackTranslation(text, sourceLanguage, targetLanguage, cacheKey, e);
        } catch (IOException | IllegalArgumentException e) {
            return fallbackTranslation(text, sourceLanguage, targetLanguage, cacheKey, e);
        }
    }

    public MessageSummaryDto summarize(MessageSummarizeRequestDto dto) {
        var summaryMessages = normalizeSummaryMessages(dto.messages());
        var roomName = normalizeRoomName(dto.roomName());
        var cacheKey = buildSummaryCacheKey(summaryMessages, roomName);

        var cached = summaryCache.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        try {
            var request = HttpRequest.newBuilder(buildCompletionsUri())
                .timeout(resolveRequestTimeout())
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + requireApiKey())
                .POST(HttpRequest.BodyPublishers.ofString(
                    buildSummaryRequestBody(summaryMessages, roomName),
                    StandardCharsets.UTF_8
                ))
                .build();

            var response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new ApiException(HttpStatus.BAD_GATEWAY, "Summary service request failed");
            }

            var summaryText = parseAssistantText(response.body());
            var result = new MessageSummaryDto(summaryText, summaryMessages.size());
            summaryCache.put(cacheKey, result);
            return result;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return fallbackSummary(summaryMessages, roomName, cacheKey, e);
        } catch (ApiException e) {
            if (!isUpstreamFailure(e)) {
                throw e;
            }

            return fallbackSummary(summaryMessages, roomName, cacheKey, e);
        } catch (IOException | IllegalArgumentException e) {
            return fallbackSummary(summaryMessages, roomName, cacheKey, e);
        }
    }

    private MessageTranslationDto fallbackTranslation(
        String text,
        String sourceLanguage,
        String targetLanguage,
        String cacheKey,
        Exception cause
    ) {
        LOGGER.warn("Translation AI fallback activated: {}", cause.getMessage());

        var detectedSourceLanguage = "auto".equals(sourceLanguage) ? "" : sourceLanguage;
        var fallback = new MessageTranslationDto(text, detectedSourceLanguage, targetLanguage);
        translationCache.put(cacheKey, fallback);
        return fallback;
    }

    private MessageSummaryDto fallbackSummary(
        List<String> summaryMessages,
        String roomName,
        String cacheKey,
        Exception cause
    ) {
        LOGGER.warn("Summary AI fallback activated: {}", cause.getMessage());

        var previewCount = Math.min(summaryMessages.size(), 8);
        var startIndex = Math.max(summaryMessages.size() - previewCount, 0);

        var text = new StringBuilder();
        text.append("AI summary is temporarily unavailable.\n");
        if (!roomName.isBlank()) {
            text.append("Chat room: ").append(roomName).append('\n');
        }
        text.append("Recent messages: ").append(summaryMessages.size()).append('\n');
        text.append("Quick recap:\n");
        for (int i = startIndex; i < summaryMessages.size(); i++) {
            text.append("- ")
                .append(compactFallbackLine(summaryMessages.get(i), 160))
                .append('\n');
        }
        text.append("\nPlease try again in a few minutes for a richer AI summary.");

        var fallback = new MessageSummaryDto(text.toString().trim(), summaryMessages.size());
        summaryCache.put(cacheKey, fallback);
        return fallback;
    }

    private String compactFallbackLine(String value, int maxLength) {
        if (value == null) {
            return "";
        }

        var normalized = value.replace('\n', ' ').trim();
        if (normalized.length() <= maxLength) {
            return normalized;
        }

        return normalized.substring(0, maxLength - 3).trim() + "...";
    }

    private Duration resolveRequestTimeout() {
        var seconds = kiloRequestTimeoutSeconds;
        if (seconds < 10) {
            seconds = 10;
        }

        return Duration.ofSeconds(seconds);
    }

    private boolean isUpstreamFailure(ApiException exception) {
        return exception.getStatusCode().value() >= 500;
    }

    private URI buildCompletionsUri() {
        var baseUrl = kiloBaseUrl == null ? "" : kiloBaseUrl.trim();
        if (baseUrl.isEmpty()) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Translation service is not configured");
        }

        var separator = baseUrl.endsWith("/") ? "" : "/";
        return URI.create(baseUrl + separator + "chat/completions");
    }

    private String requireApiKey() {
        var apiKey = kiloApiKey == null ? "" : kiloApiKey.trim();
        if (apiKey.isEmpty()) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "KILO_API_KEY is not configured");
        }
        return apiKey;
    }

    private String buildRequestBody(
        String text,
        String sourceLanguage,
        List<String> previousMessages
    ) throws IOException {
        var model = kiloModel == null ? "" : kiloModel.trim();
        if (model.isEmpty()) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Translation model is not configured");
        }

        var root = objectMapper.createObjectNode();
        root.put("model", model);
        root.put("temperature", 0.1);

        var messages = root.putArray("messages");
        messages.addObject()
            .put("role", "system")
            .put("content", TRANSLATOR_SYSTEM_PROMPT);

        var userText = new StringBuilder();
        if (!"auto".equals(sourceLanguage)) {
            userText
                .append("Source language hint: ")
                .append(sourceLanguage)
                .append("\n\n");
        }

        if (!previousMessages.isEmpty()) {
            userText.append("Conversation context (oldest to newest):\n");
            for (int i = 0; i < previousMessages.size(); i++) {
                userText
                    .append(i + 1)
                    .append(". ")
                    .append(previousMessages.get(i))
                    .append('\n');
            }
            userText.append('\n');
        }

        userText
            .append("Message to translate:\n")
            .append(text);

        messages.addObject()
            .put("role", "user")
            .put("content", userText.toString());

        return objectMapper.writeValueAsString(root);
    }

    private String buildSummaryRequestBody(
        List<String> summaryMessages,
        String roomName
    ) throws IOException {
        var model = kiloModel == null ? "" : kiloModel.trim();
        if (model.isEmpty()) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Summary model is not configured");
        }

        var root = objectMapper.createObjectNode();
        root.put("model", model);
        root.put("temperature", 0.2);

        var messages = root.putArray("messages");
        messages.addObject()
            .put("role", "system")
            .put("content", SUMMARY_SYSTEM_PROMPT);

        var userText = new StringBuilder();
        if (!roomName.isBlank()) {
            userText
                .append("Chat room: ")
                .append(roomName)
                .append("\n\n");
        }

        userText.append("Recent messages (oldest to newest):\n");
        for (int i = 0; i < summaryMessages.size(); i++) {
            userText
                .append(i + 1)
                .append(". ")
                .append(summaryMessages.get(i))
                .append('\n');
        }

        messages.addObject()
            .put("role", "user")
            .put("content", userText.toString());

        return objectMapper.writeValueAsString(root);
    }

    private List<String> normalizePreviousMessages(List<String> previousMessages) {
        if (previousMessages == null || previousMessages.isEmpty()) {
            return List.of();
        }

        var normalized = new ArrayList<String>();
        for (String message : previousMessages) {
            if (message == null) {
                continue;
            }

            var value = message.trim();
            if (value.isEmpty()) {
                continue;
            }

            if (value.length() > CONTEXT_TEXT_LIMIT) {
                value = value.substring(0, CONTEXT_TEXT_LIMIT);
            }

            normalized.add(value);
        }

        if (normalized.isEmpty()) {
            return List.of();
        }

        if (normalized.size() > CONTEXT_MESSAGE_LIMIT) {
            normalized = new ArrayList<>(
                normalized.subList(normalized.size() - CONTEXT_MESSAGE_LIMIT, normalized.size())
            );
        }

        return List.copyOf(normalized);
    }

    private String buildCacheKey(
        String text,
        String sourceLanguage,
        String targetLanguage,
        List<String> previousMessages
    ) {
        var key = new StringBuilder();
        key.append(targetLanguage)
            .append('|')
            .append(sourceLanguage)
            .append('|')
            .append(text);

        for (String previous : previousMessages) {
            key.append('\u001F').append(previous);
        }

        return key.toString();
    }

    private List<String> normalizeSummaryMessages(List<String> rawMessages) {
        if (rawMessages == null || rawMessages.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Messages to summarize are required");
        }

        var normalized = new ArrayList<String>();
        for (String rawMessage : rawMessages) {
            if (rawMessage == null) {
                continue;
            }

            var value = rawMessage.trim();
            if (value.isEmpty()) {
                continue;
            }

            if (value.length() > SUMMARY_TEXT_LIMIT) {
                value = value.substring(0, SUMMARY_TEXT_LIMIT);
            }

            normalized.add(value);
        }

        if (normalized.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Messages to summarize are required");
        }

        if (normalized.size() > SUMMARY_MESSAGE_LIMIT) {
            normalized = new ArrayList<>(
                normalized.subList(normalized.size() - SUMMARY_MESSAGE_LIMIT, normalized.size())
            );
        }

        return List.copyOf(normalized);
    }

    private String normalizeRoomName(String roomName) {
        if (roomName == null) {
            return "";
        }

        var normalized = roomName.trim();
        if (normalized.isEmpty()) {
            return "";
        }

        return normalized.length() > 120 ? normalized.substring(0, 120) : normalized;
    }

    private String buildSummaryCacheKey(List<String> summaryMessages, String roomName) {
        var key = new StringBuilder();
        key.append(roomName);
        for (String message : summaryMessages) {
            key.append('\u001F').append(message);
        }
        return key.toString();
    }

    private String parseAssistantText(String rawBody) throws IOException {
        JsonNode root = objectMapper.readTree(rawBody);
        JsonNode choices = root.path("choices");
        if (!choices.isArray() || choices.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_GATEWAY, "AI service returned an invalid response");
        }

        var contentNode = choices.path(0).path("message").path("content");
        var result = sanitizeTranslatedText(extractTextContent(contentNode));
        if (result.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_GATEWAY, "AI result is empty");
        }

        return result;
    }

    private String extractTextContent(JsonNode contentNode) {
        if (contentNode.isTextual()) {
            return contentNode.asText("");
        }

        if (!contentNode.isArray()) {
            return "";
        }

        var text = new StringBuilder();
        for (JsonNode segment : contentNode) {
            if (segment == null || segment.isNull()) {
                continue;
            }

            if (segment.isTextual()) {
                text.append(segment.asText(""));
                continue;
            }

            var type = segment.path("type").asText("");
            if ("text".equalsIgnoreCase(type)) {
                text.append(segment.path("text").asText(""));
            }
        }

        return text.toString();
    }

    private String sanitizeTranslatedText(String rawText) {
        var text = rawText == null ? "" : rawText.trim();

        if (text.startsWith("```")) {
            text = text.replaceFirst("^```[a-zA-Z0-9_-]*\\s*", "");
            text = text.replaceFirst("\\s*```$", "");
        }

        if (text.length() >= 2 && text.startsWith("\"") && text.endsWith("\"")) {
            text = text.substring(1, text.length() - 1);
        }

        return text.trim();
    }

    private String normalizeText(String text) {
        var normalized = text == null ? "" : text.trim();
        if (normalized.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Text to translate is required");
        }
        return normalized;
    }

    private String normalizeTargetLanguage(String language) {
        var normalized = normalizeLanguageCode(language);
        if (normalized.isEmpty()) {
            return "vi";
        }

        if (!normalized.startsWith("vi")) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Only Vietnamese translation is supported");
        }
        return "vi";
    }

    private String normalizeSourceLanguage(String language) {
        var normalized = normalizeLanguageCode(language);
        return normalized.isEmpty() ? "auto" : normalized;
    }

    private String normalizeLanguageCode(String language) {
        if (language == null) {
            return "";
        }

        var normalized = language.trim().replace('_', '-').toLowerCase(Locale.ROOT);
        if (normalized.isEmpty()) {
            return "";
        }

        return normalized.length() > 16 ? normalized.substring(0, 16) : normalized;
    }
}
