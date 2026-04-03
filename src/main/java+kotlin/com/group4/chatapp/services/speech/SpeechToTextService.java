package com.group4.chatapp.services.speech;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.group4.chatapp.dtos.speech.SpeechToTextResponseDto;
import com.group4.chatapp.exceptions.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

@Service
public class SpeechToTextService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpeechToTextService.class);
    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(10);

    private static final int MAX_PROMPT_LENGTH = 240;
    private static final Set<String> SUPPORTED_LANGUAGE_CODES = Set.of("auto", "vi", "en", "ja");
    private static final Map<String, String> LANGUAGE_ALIASES = Map.ofEntries(
        Map.entry("vi-vn", "vi"),
        Map.entry("vietnamese", "vi"),
        Map.entry("en-us", "en"),
        Map.entry("en-gb", "en"),
        Map.entry("english", "en"),
        Map.entry("ja-jp", "ja"),
        Map.entry("jp", "ja"),
        Map.entry("japanese", "ja")
    );

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${speech-to-text.whisper.base-url:http://whisper:8000}")
    private String whisperBaseUrl;

    @Value("${speech-to-text.whisper.request-timeout-seconds:60}")
    private int whisperRequestTimeoutSeconds;

    @Value("${speech-to-text.default-language:auto}")
    private String defaultLanguage;

    @Value("${speech-to-text.max-audio-size-bytes:12582912}")
    private long maxAudioSizeBytes;

    public SpeechToTextResponseDto transcribe(
        MultipartFile audio,
        String language,
        String prompt
    ) {
        validateAudio(audio);

        var normalizedLanguage = normalizeLanguage(language);
        var normalizedPrompt = normalizePrompt(prompt);

        try {
            var response = sendWhisperRequest(audio, normalizedLanguage, normalizedPrompt);
            var text = parseText(response.getBody());
            if (text.isBlank()) {
                throw new ApiException(HttpStatus.BAD_GATEWAY, "Speech-to-text result is empty");
            }

            return new SpeechToTextResponseDto(text);
        } catch (HttpStatusCodeException e) {
            var upstreamStatus = HttpStatus.resolve(e.getStatusCode().value());
            var upstreamBody = normalizeSimpleText(e.getResponseBodyAsString(StandardCharsets.UTF_8), "");

            if (upstreamStatus == HttpStatus.REQUEST_TIMEOUT || upstreamStatus == HttpStatus.GATEWAY_TIMEOUT) {
                throw new ApiException(HttpStatus.GATEWAY_TIMEOUT, "Speech-to-text timeout");
            }

            if (upstreamStatus != null && upstreamStatus.is4xxClientError()) {
                var detail = extractErrorMessage(upstreamBody);
                if (detail.isBlank()) {
                    detail = "Speech-to-text request is invalid";
                }

                LOGGER.warn(
                    "Speech-to-text upstream rejected request: status {}, body {}",
                    upstreamStatus.value(),
                    trimForLog(upstreamBody)
                );
                throw new ApiException(HttpStatus.BAD_REQUEST, detail);
            }

            LOGGER.warn(
                "Speech-to-text upstream request failed: status {}, body {}",
                e.getStatusCode().value(),
                trimForLog(upstreamBody)
            );
            throw new ApiException(HttpStatus.BAD_GATEWAY, "Speech-to-text upstream request failed");
        } catch (ResourceAccessException e) {
            var rootMessage = e.getMostSpecificCause() == null
                ? e.getMessage()
                : e.getMostSpecificCause().getMessage();

            if (rootMessage != null && rootMessage.toLowerCase(Locale.ROOT).contains("timed out")) {
                throw new ApiException(HttpStatus.GATEWAY_TIMEOUT, "Speech-to-text timeout");
            }

            LOGGER.warn("Speech-to-text request failed: {}", rootMessage);
            throw new ApiException(HttpStatus.BAD_GATEWAY, "Speech-to-text service unavailable");
        } catch (ApiException e) {
            throw e;
        } catch (IOException | IllegalArgumentException e) {
            LOGGER.warn("Speech-to-text request failed: {}", e.getMessage());
            throw new ApiException(HttpStatus.BAD_GATEWAY, "Speech-to-text service unavailable");
        }
    }

    private void validateAudio(MultipartFile audio) {
        if (audio == null || audio.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Audio file is required");
        }

        if (maxAudioSizeBytes > 0 && audio.getSize() > maxAudioSizeBytes) {
            throw new ApiException(HttpStatus.PAYLOAD_TOO_LARGE, "Audio file is too large");
        }
    }

    private Duration resolveRequestTimeout() {
        var seconds = whisperRequestTimeoutSeconds;
        if (seconds < 10) {
            seconds = 10;
        }

        return Duration.ofSeconds(seconds);
    }

    private URI buildSpeechToTextUri() {
        var baseUrl = whisperBaseUrl == null ? "" : whisperBaseUrl.trim();
        if (baseUrl.isEmpty()) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Whisper service base URL is not configured");
        }

        var separator = baseUrl.endsWith("/") ? "" : "/";
        return URI.create(baseUrl + separator + "speech-to-text");
    }

    private ResponseEntity<String> sendWhisperRequest(
        MultipartFile audio,
        String language,
        String prompt
    ) throws IOException {
        var body = new LinkedMultiValueMap<String, Object>();
        body.add("language", language);

        if (!prompt.isBlank()) {
            body.add("prompt", prompt);
        }

        body.add("audio", buildAudioPart(audio));

        var headers = new HttpHeaders();
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        var requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(CONNECT_TIMEOUT);
        requestFactory.setReadTimeout(resolveRequestTimeout());

        var restTemplate = new RestTemplate(requestFactory);
        var requestEntity = new HttpEntity<>(body, headers);
        return restTemplate.exchange(buildSpeechToTextUri(), HttpMethod.POST, requestEntity, String.class);
    }

    private HttpEntity<ByteArrayResource> buildAudioPart(MultipartFile audio) throws IOException {
        var filename = resolveFilename(audio);
        var resource = new ByteArrayResource(audio.getBytes()) {
            @Override
            public String getFilename() {
                return filename;
            }
        };

        var headers = new HttpHeaders();
        headers.setContentType(resolveContentType(audio));
        headers.setContentDispositionFormData("audio", filename);
        return new HttpEntity<>(resource, headers);
    }

    private String parseText(String responseBody) throws IOException {
        JsonNode root = objectMapper.readTree(responseBody);
        return root.path("text").asText("").trim();
    }

    private String extractErrorMessage(String responseBody) {
        if (responseBody == null || responseBody.isBlank()) {
            return "";
        }

        try {
            JsonNode root = objectMapper.readTree(responseBody);

            var detail = root.path("detail").asText("").trim();
            if (!detail.isBlank()) {
                return detail;
            }

            var title = root.path("title").asText("").trim();
            if (!title.isBlank()) {
                return title;
            }
        } catch (IOException ignored) {
            // Keep raw body fallback when upstream did not return JSON.
        }

        return responseBody.trim();
    }

    private String trimForLog(String value) {
        if (value == null) {
            return "";
        }

        var trimmed = value.replaceAll("\\s+", " ").trim();
        if (trimmed.length() > 300) {
            return trimmed.substring(0, 300) + "...";
        }

        return trimmed;
    }

    private String normalizeLanguage(String language) {
        var fallback = canonicalizeLanguage(normalizeSimpleText(defaultLanguage, "auto"));
        if (fallback.isEmpty()) {
            fallback = "auto";
        }

        var normalized = canonicalizeLanguage(normalizeSimpleText(language, fallback));
        if (normalized.isEmpty()) {
            normalized = fallback;
        }

        if (!SUPPORTED_LANGUAGE_CODES.contains(normalized)) {
            throw new ApiException(
                HttpStatus.BAD_REQUEST,
                "Invalid language parameter. Supported values: auto, vi, en, ja"
            );
        }

        return normalized;
    }

    private String canonicalizeLanguage(String language) {
        var normalized = language.trim().toLowerCase(Locale.ROOT).replace('_', '-');
        return LANGUAGE_ALIASES.getOrDefault(normalized, normalized);
    }

    private String normalizePrompt(String prompt) {
        if (prompt == null) {
            return "";
        }

        var normalized = prompt.trim();
        if (normalized.length() > MAX_PROMPT_LENGTH) {
            return normalized.substring(0, MAX_PROMPT_LENGTH);
        }

        return normalized;
    }

    private String normalizeSimpleText(String value, String fallback) {
        var normalized = value == null ? "" : value.trim();
        if (normalized.isEmpty()) {
            return fallback;
        }

        return normalized;
    }

    private String resolveFilename(MultipartFile audio) {
        var original = audio.getOriginalFilename();
        var normalized = original == null ? "" : original.trim();
        if (normalized.isEmpty()) {
            return "audio_upload.bin";
        }

        normalized = normalized.replace('\\', '/');
        var slashIndex = normalized.lastIndexOf('/');
        if (slashIndex >= 0 && slashIndex + 1 < normalized.length()) {
            normalized = normalized.substring(slashIndex + 1);
        }

        normalized = normalized.replace('"', '_').replace(';', '_');
        if (normalized.isBlank()) {
            return "audio_upload.bin";
        }

        return normalized;
    }

    private MediaType resolveContentType(MultipartFile audio) {
        var contentType = audio.getContentType();
        if (contentType == null || contentType.isBlank()) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }

        try {
            return MediaType.parseMediaType(contentType);
        } catch (InvalidMediaTypeException ignored) {
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }
}
