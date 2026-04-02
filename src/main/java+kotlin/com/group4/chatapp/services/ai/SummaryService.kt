package com.group4.chatapp.services.ai

import com.group4.chatapp.dtos.messages.MessageSummarizeRequestDto
import com.group4.chatapp.dtos.messages.MessageSummaryDto
import com.group4.chatapp.exceptions.ApiException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.util.Collections

@Service
class SummaryService(
    private val openAIClientService: OpenAIClientService,
    private val promptService: PromptService
) {

    fun summarize(dto: MessageSummarizeRequestDto): MessageSummaryDto {

        val summaryMessages = normalizeSummaryMessages(dto.messages())
        val roomName = normalizeRoomName(dto.roomName())
        val cacheKey = buildSummaryCacheKey(summaryMessages, roomName)

        summaryCache[cacheKey]?.let { return it }

        return try {

            val prompt = promptService.buildSummaryPrompt(summaryMessages, roomName)
            val summaryText = openAIClientService.requestText(
                prompt, "Summary service", 0.2
            )
            if (summaryText.isBlank()) {
                throw ApiException(HttpStatus.BAD_GATEWAY, "AI result is empty")
            }

            MessageSummaryDto(summaryText, summaryMessages.size)
                .also { summaryCache[cacheKey] = it }

        } catch (ex: Exception) {
            fallbackSummary(summaryMessages, roomName, cacheKey, ex)
        }
    }

    private fun fallbackSummary(
        summaryMessages: List<String>,
        roomName: String,
        cacheKey: String,
        cause: Exception
    ): MessageSummaryDto {
        logger.warn("Summary AI fallback activated: {}", cause.message)

        val previewCount = minOf(summaryMessages.size, 8)
        val startIndex = maxOf(summaryMessages.size - previewCount, 0)

        val text = buildString {
            append("AI summary is temporarily unavailable.\n")
            if (roomName.isNotBlank()) {
                append("Chat room: ")
                append(roomName)
                append('\n')
            }
            append("Recent messages: ")
            append(summaryMessages.size)
            append('\n')
            append("Quick recap:\n")
            for (index in startIndex until summaryMessages.size) {
                append("- ")
                append(compactFallbackLine(summaryMessages[index], 160))
                append('\n')
            }
            append("\nPlease try again in a few minutes for a richer AI summary.")
        }

        return MessageSummaryDto(text.trim(), summaryMessages.size)
            .also { summaryCache[cacheKey] = it }
    }

    private fun compactFallbackLine(value: String?, maxLength: Int): String {
        val normalized = value?.replace('\n', ' ')?.trim().orEmpty()
        if (normalized.length <= maxLength) {
            return normalized
        }

        return normalized.substring(0, maxLength - 3).trim() + "..."
    }

    private fun normalizeSummaryMessages(rawMessages: List<String>?): List<String> {
        if (rawMessages.isNullOrEmpty()) {
            throw ApiException(
                HttpStatus.BAD_REQUEST,
                "Messages to summarize are required"
            )
        }

        val normalized = rawMessages.asSequence()
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .map { it.take(SUMMARY_TEXT_LIMIT) }
            .toList()

        if (normalized.isEmpty()) {
            throw ApiException(
                HttpStatus.BAD_REQUEST,
                "Messages to summarize are required"
            )
        }

        return normalized.takeLast(SUMMARY_MESSAGE_LIMIT)
    }

    private fun normalizeRoomName(roomName: String?): String {
        val normalized = roomName?.trim().orEmpty()
        if (normalized.isEmpty()) {
            return ""
        }

        return normalized.take(120)
    }

    private fun buildSummaryCacheKey(
        summaryMessages: List<String>,
        roomName: String
    ): String = buildString {
        append(roomName)
        summaryMessages.forEach {
            append('\u001F')
            append(it)
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(SummaryService::class.java)

        private const val SUMMARY_CACHE_MAX_SIZE = 1024
        private const val SUMMARY_MESSAGE_LIMIT = 40
        private const val SUMMARY_TEXT_LIMIT = 500
    }

    private val summaryCache = Collections.synchronizedMap(
        object : LinkedHashMap<String, MessageSummaryDto>(SUMMARY_CACHE_MAX_SIZE + 1, 0.75f, true) {
            override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, MessageSummaryDto>?): Boolean {
                return size > SUMMARY_CACHE_MAX_SIZE
            }
        }
    )
}
