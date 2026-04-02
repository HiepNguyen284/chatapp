package com.group4.chatapp.services.chatbot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group4.chatapp.dtos.chatbot.ChatbotConversationCreateDto;
import com.group4.chatapp.dtos.chatbot.ChatbotConversationDto;
import com.group4.chatapp.dtos.chatbot.ChatbotMessageDto;
import com.group4.chatapp.dtos.chatbot.ChatbotStreamDoneDto;
import com.group4.chatapp.dtos.chatbot.ChatbotStreamErrorDto;
import com.group4.chatapp.dtos.chatbot.ChatbotStreamRequestDto;
import com.group4.chatapp.dtos.chatbot.ChatbotStreamTokenDto;
import com.group4.chatapp.exceptions.ApiException;
import com.group4.chatapp.models.ChatbotConversation;
import com.group4.chatapp.models.ChatbotMessage;
import com.group4.chatapp.repositories.ChatbotConversationRepository;
import com.group4.chatapp.repositories.ChatbotMessageRepository;
import com.group4.chatapp.services.UserService;
import jakarta.annotation.PreDestroy;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
public class ChatbotService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatbotService.class);

    private static final int HISTORY_LIMIT_FOR_PROMPT = 24;
    private static final int PREVIEW_MAX_LENGTH = 120;

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(10))
        .build();

    private static final ExecutorService STREAM_EXECUTOR = Executors.newCachedThreadPool();

    private static final String ASSISTANT_SYSTEM_PROMPT = """
        You are an AI chatbot for a messaging app.
        Reply in Vietnamese by default unless the user requests another language.
        Be concise, practical, and friendly.
        Use markdown when it improves readability.
        If information is uncertain, say so clearly.
        """;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final UserService userService;
    private final ChatbotConversationRepository chatbotConversationRepository;
    private final ChatbotMessageRepository chatbotMessageRepository;

    @Value("${agents.messages.base-url:}")
    private String llmBaseUrl;

    @Value("${agents.chatbot.model:${agents.messages.model:}}")
    private String chatbotModel;

    @Value("${agents.messages.api-key:}")
    private String llmApiKey;

    @Value("${agents.chatbot.stream-timeout-seconds:120}")
    private int chatbotStreamTimeoutSeconds;

    @PreDestroy
    void shutdownStreamingExecutor() {
        STREAM_EXECUTOR.shutdown();
    }

    @Transactional
    public ChatbotConversationDto createConversation(ChatbotConversationCreateDto dto) {
        var user = userService.getUserOrThrows();

        var conversation = ChatbotConversation.builder()
            .owner(user)
            .title(normalizeTitle(dto.title()))
            .modelName(resolveDefaultModel())
            .mcpEnabled(false)
            .build();

        var saved = chatbotConversationRepository.save(conversation);
        return new ChatbotConversationDto(saved, "");
    }

    @Transactional
    public List<ChatbotConversationDto> listConversations() {
        var user = userService.getUserOrThrows();
        return chatbotConversationRepository.findByOwner_IdOrderByUpdatedOnDescCreatedOnDesc(user.getId())
            .stream()
            .map(conversation -> {
                var preview = chatbotMessageRepository
                    .findFirstByConversation_IdOrderByCreatedOnDescIdDesc(conversation.getId())
                    .map(this::buildPreview)
                    .orElse("");
                return new ChatbotConversationDto(conversation, preview);
            })
            .toList();
    }

    @Transactional
    public List<ChatbotMessageDto> listMessages(long conversationId) {
        var user = userService.getUserOrThrows();
        var conversation = getConversationAsOwner(conversationId, user.getId());
        return chatbotMessageRepository.findByConversation_IdOrderByCreatedOnAscIdAsc(conversation.getId())
            .stream()
            .map(ChatbotMessageDto::new)
            .toList();
    }

    @Transactional
    public void deleteConversation(long conversationId) {
        var user = userService.getUserOrThrows();
        var conversation = getConversationAsOwner(conversationId, user.getId());

        chatbotMessageRepository.deleteByConversation_Id(conversation.getId());
        chatbotConversationRepository.delete(conversation);
    }

    @Transactional
    public SseEmitter streamReply(long conversationId, ChatbotStreamRequestDto dto) {
        var user = userService.getUserOrThrows();
        var conversation = getConversationAsOwner(conversationId, user.getId());

        var userPrompt = normalizePrompt(dto.message());
        var useMcp = Boolean.TRUE.equals(dto.useMcp());
        var mcpSessionId = normalizeMcpSessionId(dto.mcpSessionId());
        var mcpMetadata = normalizeMcpMetadata(dto.mcpMetadata());
        var modelName = normalizeModel(dto.model(), conversation.getModelName());

        conversation.setModelName(modelName);
        conversation.setMcpEnabled(useMcp);
        conversation.setMcpSessionId(mcpSessionId.isBlank() ? null : mcpSessionId);
        conversation.setMcpMetadata(mcpMetadata.isBlank() ? null : mcpMetadata);
        chatbotConversationRepository.save(conversation);

        chatbotMessageRepository.save(ChatbotMessage.builder()
            .conversation(conversation)
            .role(ChatbotMessage.Role.USER)
            .content(userPrompt)
            .build());

        touchConversation(conversation);

        var promptMessages = chatbotMessageRepository.findRecentByConversationId(
            conversation.getId(),
            PageRequest.of(0, HISTORY_LIMIT_FOR_PROMPT)
        );
        Collections.reverse(promptMessages);

        var emitter = new SseEmitter(0L);

        STREAM_EXECUTOR.submit(() -> streamAssistantResponse(
            conversation,
            promptMessages,
            modelName,
            useMcp,
            mcpSessionId,
            mcpMetadata,
            emitter
        ));

        return emitter;
    }

    private void streamAssistantResponse(
        ChatbotConversation conversation,
        List<ChatbotMessage> promptMessages,
        String modelName,
        boolean useMcp,
        String mcpSessionId,
        String mcpMetadata,
        SseEmitter emitter
    ) {
        try {
            var request = HttpRequest.newBuilder(buildCompletionsUri())
                .timeout(resolveStreamTimeout())
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + requireApiKey())
                .POST(HttpRequest.BodyPublishers.ofString(
                    buildStreamRequestBody(
                        promptMessages,
                        modelName,
                        useMcp,
                        mcpSessionId,
                        mcpMetadata
                    ),
                    StandardCharsets.UTF_8
                ))
                .build();

            var response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofLines());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new ApiException(HttpStatus.BAD_GATEWAY, "Chatbot request failed");
            }

            var assistantTextBuilder = new StringBuilder();

            try (var lines = response.body()) {
                lines.forEach(line -> {
                    try {
                        readStreamingLine(line, assistantTextBuilder, emitter);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
            } catch (UncheckedIOException e) {
                throw e.getCause();
            }

            var assistantText = sanitizeAssistantText(assistantTextBuilder.toString());
            if (assistantText.isBlank()) {
                throw new ApiException(HttpStatus.BAD_GATEWAY, "Chatbot result is empty");
            }

            var assistantMessage = chatbotMessageRepository.save(ChatbotMessage.builder()
                .conversation(conversation)
                .role(ChatbotMessage.Role.ASSISTANT)
                .content(assistantText)
                .build());

            touchConversation(conversation);

            emitter.send(SseEmitter.event()
                .name("done")
                .data(
                    new ChatbotStreamDoneDto(
                        conversation.getId(),
                        assistantMessage.getId(),
                        assistantText
                    ),
                    MediaType.APPLICATION_JSON
                ));

            emitter.complete();
        } catch (Exception e) {
            streamFallbackResponse(conversation, promptMessages, emitter, e);
        }
    }

    private void streamFallbackResponse(
        ChatbotConversation conversation,
        List<ChatbotMessage> promptMessages,
        SseEmitter emitter,
        Exception cause
    ) {
        LOGGER.warn(
            "Chatbot upstream unavailable for conversation {}: {}",
            conversation.getId(),
            cause.getMessage()
        );

        var fallbackText = buildFallbackAssistantText(promptMessages);

        try {
            var assistantMessage = chatbotMessageRepository.save(ChatbotMessage.builder()
                .conversation(conversation)
                .role(ChatbotMessage.Role.ASSISTANT)
                .content(fallbackText)
                .build());

            touchConversation(conversation);

            emitter.send(SseEmitter.event()
                .name("token")
                .data(new ChatbotStreamTokenDto(fallbackText), MediaType.APPLICATION_JSON));

            emitter.send(SseEmitter.event()
                .name("done")
                .data(
                    new ChatbotStreamDoneDto(
                        conversation.getId(),
                        assistantMessage.getId(),
                        fallbackText
                    ),
                    MediaType.APPLICATION_JSON
                ));

            emitter.complete();
        } catch (Exception fallbackException) {
            LOGGER.error("Failed to emit chatbot fallback response", fallbackException);
            sendErrorEvent(emitter, friendlyErrorMessage(cause));
        }
    }

    private String buildFallbackAssistantText(List<ChatbotMessage> promptMessages) {
        String latestUserPrompt = "";

        for (int i = promptMessages.size() - 1; i >= 0; i--) {
            var item = promptMessages.get(i);
            if (item.getRole() != ChatbotMessage.Role.USER) {
                continue;
            }

            latestUserPrompt = item.getContent() == null ? "" : item.getContent().trim();
            break;
        }

        var preview = latestUserPrompt.isBlank()
            ? "No question text was captured."
            : compactFallbackLine(latestUserPrompt, 220);

        return """
I cannot reach the AI provider right now.
Your message has been saved, and you can retry in a moment.

Your latest request:
- %s
""".formatted(preview);
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

    private Duration resolveStreamTimeout() {
        var seconds = chatbotStreamTimeoutSeconds;
        if (seconds < 20) {
            seconds = 20;
        }

        return Duration.ofSeconds(seconds);
    }

    private void readStreamingLine(
        String line,
        StringBuilder assistantTextBuilder,
        SseEmitter emitter
    ) throws IOException {
        var normalized = line == null ? "" : line.trim();
        if (!normalized.startsWith("data:")) {
            return;
        }

        var payload = normalized.substring("data:".length()).trim();
        if (payload.isBlank() || "[DONE]".equals(payload)) {
            return;
        }

        var chunk = objectMapper.readTree(payload);
        var tokenNode = chunk.path("choices").path(0).path("delta").path("content");
        var token = extractTextContent(tokenNode);
        if (token.isBlank()) {
            return;
        }

        assistantTextBuilder.append(token);

        emitter.send(SseEmitter.event()
            .name("token")
            .data(new ChatbotStreamTokenDto(token), MediaType.APPLICATION_JSON));
    }

    private String buildStreamRequestBody(
        List<ChatbotMessage> promptMessages,
        String modelName,
        boolean useMcp,
        String mcpSessionId,
        String mcpMetadata
    ) throws IOException {
        var root = objectMapper.createObjectNode();
        root.put("model", modelName);
        root.put("temperature", 0.4);
        root.put("stream", true);

        var messages = root.putArray("messages");

        var systemPrompt = new StringBuilder(ASSISTANT_SYSTEM_PROMPT);
        if (useMcp) {
            systemPrompt
                .append("\nMCP mode is requested by the user. ")
                .append("If external tools are not available yet, answer with your own knowledge and state the limitation briefly.");

            if (!mcpSessionId.isBlank()) {
                systemPrompt.append("\nMCP session id: ").append(mcpSessionId);
            }

            if (!mcpMetadata.isBlank()) {
                systemPrompt.append("\nMCP context hint: ").append(mcpMetadata);
            }
        }

        messages.addObject()
            .put("role", "system")
            .put("content", systemPrompt.toString());

        for (ChatbotMessage item : promptMessages) {
            messages.addObject()
                .put("role", mapRole(item.getRole()))
                .put("content", item.getContent());
        }

        return objectMapper.writeValueAsString(root);
    }

    private String mapRole(ChatbotMessage.Role role) {
        if (role == null) {
            return "user";
        }

        return switch (role) {
            case ASSISTANT -> "assistant";
            case SYSTEM -> "system";
            case TOOL -> "tool";
            case USER -> "user";
        };
    }

    private ChatbotConversation getConversationAsOwner(long conversationId, long ownerId) {
        return chatbotConversationRepository.findByIdAndOwner_Id(conversationId, ownerId)
            .orElseThrow(() -> new ApiException(
                HttpStatus.NOT_FOUND,
                "Chatbot conversation not found"
            ));
    }

    private void touchConversation(ChatbotConversation conversation) {
        conversation.setUpdatedOn(new Timestamp(System.currentTimeMillis()));
        chatbotConversationRepository.save(conversation);
    }

    private String buildPreview(ChatbotMessage message) {
        var prefix = message.getRole() == ChatbotMessage.Role.USER ? "Bạn: " : "AI: ";
        var normalized = (message.getContent() == null ? "" : message.getContent())
            .replace('\n', ' ')
            .trim();
        if (normalized.length() > PREVIEW_MAX_LENGTH) {
            normalized = normalized.substring(0, PREVIEW_MAX_LENGTH).trim() + "...";
        }

        return prefix + normalized;
    }

    private URI buildCompletionsUri() {
        var baseUrl = llmBaseUrl == null ? "" : llmBaseUrl.trim();
        if (baseUrl.isEmpty()) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Chatbot service is not configured");
        }

        var separator = baseUrl.endsWith("/") ? "" : "/";
        return URI.create(baseUrl + separator + "chat/completions");
    }

    private String requireApiKey() {
        var apiKey = llmApiKey == null ? "" : llmApiKey.trim();
        if (apiKey.isEmpty()) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "LLM_API_KEY is not configured");
        }

        return apiKey;
    }

    private String resolveDefaultModel() {
        var model = chatbotModel == null ? "" : chatbotModel.trim();
        if (model.isEmpty()) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Chatbot model is not configured");
        }
        return model;
    }

    private String normalizeTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            return "Cuộc trò chuyện mới";
        }

        var normalized = title.trim();
        return normalized.length() > 120 ? normalized.substring(0, 120) : normalized;
    }

    private String normalizePrompt(String message) {
        var normalized = message == null ? "" : message.trim();
        if (normalized.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Message is required");
        }

        return normalized.length() > 4000 ? normalized.substring(0, 4000) : normalized;
    }

    private String normalizeModel(String requestedModel, String fallbackModel) {
        var candidate = requestedModel == null ? "" : requestedModel.trim();
        if (candidate.isEmpty()) {
            candidate = fallbackModel == null ? "" : fallbackModel.trim();
        }
        if (candidate.isEmpty()) {
            candidate = resolveDefaultModel();
        }
        return candidate.length() > 80 ? candidate.substring(0, 80) : candidate;
    }

    private String normalizeMcpSessionId(String mcpSessionId) {
        if (mcpSessionId == null) {
            return "";
        }

        var normalized = mcpSessionId.trim();
        return normalized.length() > 120 ? normalized.substring(0, 120) : normalized;
    }

    private String normalizeMcpMetadata(String mcpMetadata) {
        if (mcpMetadata == null) {
            return "";
        }

        var normalized = mcpMetadata.trim();
        return normalized.length() > 2000 ? normalized.substring(0, 2000) : normalized;
    }

    private String sanitizeAssistantText(String rawText) {
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

    private void sendErrorEvent(SseEmitter emitter, String message) {
        try {
            emitter.send(SseEmitter.event()
                .name("error")
                .data(new ChatbotStreamErrorDto(message), MediaType.APPLICATION_JSON));
        } catch (Exception ignored) {
            // Ignore send failures when connection is already closed.
        }

        emitter.complete();
    }

    private String friendlyErrorMessage(Exception e) {
        var message = e.getMessage();
        if (message == null || message.isBlank()) {
            return "Chatbot stream failed";
        }

        return message;
    }
}
