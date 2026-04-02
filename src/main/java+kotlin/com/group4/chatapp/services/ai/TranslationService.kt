package com.group4.chatapp.services.ai

import com.group4.chatapp.dtos.messages.MessageTranslateRequestDto
import com.group4.chatapp.dtos.messages.MessageTranslationDto
import com.group4.chatapp.exceptions.ApiException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.util.Collections
import java.util.Locale

@Service
class TranslationService(
    private val openAIClientService: OpenAIClientService,
    private val promptService: PromptService
) {

    fun translate(dto: MessageTranslateRequestDto): MessageTranslationDto {
        val text = normalizeText(dto.text())
        val sourceLanguage = normalizeSourceLanguage(dto.sourceLanguage())
        val targetLanguage = normalizeTargetLanguage(dto.targetLanguage())
        val previousMessages = normalizePreviousMessages(dto.previousMessages())
        val cacheKey =
            buildTranslationCacheKey(text, sourceLanguage, targetLanguage, previousMessages)

        translationCache[cacheKey]?.let { return it }

        return try {

            val prompt = promptService.buildTranslationPrompt(
                text, sourceLanguage,
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
                .also { translationCache[cacheKey] = it }

        } catch (ex: Exception) {
            fallbackTranslation(text, sourceLanguage, targetLanguage, cacheKey, ex)
        }
    }

    private fun fallbackTranslation(
        text: String,
        sourceLanguage: String,
        targetLanguage: String,
        cacheKey: String,
        cause: Exception
    ): MessageTranslationDto {

        logger.warn("Translation AI fallback activated: {}", cause.message)

        val detectedSourceLanguage = if (sourceLanguage == "auto") "" else sourceLanguage
        return MessageTranslationDto(text, detectedSourceLanguage, targetLanguage)
            .also { translationCache[cacheKey] = it }
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
        if (normalized.isEmpty()) {
            return "vi"
        }

        if (!normalized.startsWith("vi")) {
            throw ApiException(HttpStatus.BAD_REQUEST, "Only Vietnamese translation is supported")
        }

        return "vi"
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

        private const val CACHE_MAX_SIZE = 2048
        private const val CONTEXT_MESSAGE_LIMIT = 8
        private const val CONTEXT_TEXT_LIMIT = 500
    }

    private val translationCache = Collections.synchronizedMap(
        object : LinkedHashMap<String, MessageTranslationDto>(CACHE_MAX_SIZE + 1, 0.75f, true) {
            override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, MessageTranslationDto>?): Boolean {
                return size > CACHE_MAX_SIZE
            }
        }
    )
}
