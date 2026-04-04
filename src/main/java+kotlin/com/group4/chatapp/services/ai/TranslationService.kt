package com.group4.chatapp.services.ai

import com.group4.chatapp.dtos.messages.MessageTranslateRequestDto
import com.group4.chatapp.dtos.messages.MessageTranslationDto
import com.group4.chatapp.exceptions.ApiException
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.Locale

@Service
class TranslationService(
    private val openAIClientService: OpenAIClientService,
    private val promptService: PromptService,
    private val redisTemplate: StringRedisTemplate
) {

    fun translate(dto: MessageTranslateRequestDto): MessageTranslationDto {
        val text = normalizeText(dto.text())
        val sourceLanguage = normalizeSourceLanguage(dto.sourceLanguage())
        val targetLanguage = normalizeTargetLanguage(dto.targetLanguage())
        val previousMessages = normalizePreviousMessages(dto.previousMessages())
        val cacheKey =
            buildTranslationCacheKey(text, sourceLanguage, targetLanguage, previousMessages)

        getCachedTranslation(cacheKey, targetLanguage)?.let { return it }

        return try {

            val prompt = promptService.buildTranslationPrompt(
                text, sourceLanguage, targetLanguage,
                previousMessages
            )

            val translatedText = openAIClientService.requestText(
                prompt, "Translation service", 0.1
            )

            if (translatedText.isEmpty()) {
                throw ApiException(HttpStatus.BAD_GATEWAY, "AI result is empty")
            }

            var detectedSourceLanguage = sourceLanguage
            if (sourceLanguage == "auto") {
                detectedSourceLanguage = ""
            }

            MessageTranslationDto(translatedText, detectedSourceLanguage, targetLanguage)
                .also { cacheTranslation(cacheKey, it) }

        } catch (ex: Exception) {
            fallbackTranslation(ex)
        }
    }

    private fun getCachedTranslation(cacheKey: String, targetLanguage: String): MessageTranslationDto? {
        return try {
            val translatedText = redisTemplate.opsForValue().get(TRANSLATION_CACHE_PREFIX + cacheKey)
            if (translatedText != null) {
                MessageTranslationDto(translatedText, "", targetLanguage)
            } else null
        } catch (e: Exception) {
            null
        }
    }

    private fun cacheTranslation(cacheKey: String, dto: MessageTranslationDto) {
        try {
            redisTemplate.opsForValue().set(
                TRANSLATION_CACHE_PREFIX + cacheKey,
                dto.translatedText,
                CACHE_TTL
            )
        } catch (e: Exception) {
            logger.warn("Failed to cache translation in Redis: {}", e.message)
        }
    }

    private fun fallbackTranslation(
        cause: Exception
    ): MessageTranslationDto {

        logger.warn("Translation AI fallback activated: {}", cause.message)

        if (cause is ApiException && cause.statusCode.value() == HttpStatus.TOO_MANY_REQUESTS.value()) {
            throw ApiException(
                HttpStatus.TOO_MANY_REQUESTS,
                "Translation AI is rate limited, please try again shortly"
            )
        }

        throw ApiException(
            HttpStatus.BAD_GATEWAY,
            "Translation service is temporarily unavailable"
        )
    }

    private fun normalizePreviousMessages(previousMessages: List<String>?): List<String> {
        if (previousMessages.isNullOrEmpty()) {
            return emptyList()
        }

        val normalized = previousMessages.asSequence()
            .mapNotNull { it?.trim() }
            .filter { it.isNotEmpty() }
            .map { it.take(CONTEXT_TEXT_LIMIT) }
            .toList()

        if (normalized.isEmpty()) {
            return emptyList()
        }

        return normalized.takeLast(CONTEXT_MESSAGE_LIMIT)
    }

    private fun buildTranslationCacheKey(
        text: String,
        sourceLanguage: String,
        targetLanguage: String,
        previousMessages: List<String>
    ): String = buildString {
        append(targetLanguage)
        append('|')
        append(sourceLanguage)
        append('|')
        append(text)
        previousMessages.forEach {
            append('\u001F')
            append(it)
        }
    }

    private fun normalizeText(text: String?): String {
        val normalized = text?.trim().orEmpty()
        if (normalized.isEmpty()) {
            throw ApiException(HttpStatus.BAD_REQUEST, "Text to translate is required")
        }
        return normalized
    }

    private fun normalizeTargetLanguage(language: String?): String {
        val normalized = normalizeLanguageCode(language)
        return normalized.ifEmpty { "vi" }
    }

    private fun normalizeSourceLanguage(language: String?): String {
        val normalized = normalizeLanguageCode(language)
        return normalized.ifEmpty { "auto" }
    }

    private fun normalizeLanguageCode(language: String?): String {
        val normalized = language
            ?.trim()
            ?.replace('_', '-')
            ?.lowercase(Locale.ROOT)
            .orEmpty()

        if (normalized.isEmpty()) {
            return ""
        }

        return normalized.take(16)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(TranslationService::class.java)

        private const val TRANSLATION_CACHE_PREFIX = "translation:"
        private val CACHE_TTL = Duration.ofHours(24)
        private const val CONTEXT_MESSAGE_LIMIT = 8
        private const val CONTEXT_TEXT_LIMIT = 500
    }
}
