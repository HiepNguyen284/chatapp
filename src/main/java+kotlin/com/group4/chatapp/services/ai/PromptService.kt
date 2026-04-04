package com.group4.chatapp.services.ai

import org.springframework.stereotype.Service

@Service
class PromptService {

    fun buildTranslationPrompt(
        text: String,
        sourceLanguage: String,
        targetLanguage: String,
        previousMessages: List<String>
    ): PromptSpec {
        val normalizedTargetLanguage = targetLanguage.trim().ifEmpty { "vi" }
        val targetLanguageName = languageDisplayName(normalizedTargetLanguage)

        val userPrompt = buildString {
            append("Target language: ")
            append(targetLanguageName)
            append(" (code: ")
            append(normalizedTargetLanguage)
            append(")\n\n")

            if (sourceLanguage != "auto") {
                append("Source language hint: ")
                append(sourceLanguage)
                append("\n\n")
            }

            if (previousMessages.isNotEmpty()) {
                append("Conversation context (oldest to newest):\n")
                previousMessages.forEachIndexed { index, message ->
                    append(index + 1)
                    append(". ")
                    append(message)
                    append('\n')
                }
                append('\n')
            }

            append("Message to translate:\n")
            append(text)
        }

        return PromptSpec(
            systemPrompt = buildTranslationSystemPrompt(
                targetLanguageName,
                normalizedTargetLanguage
            ),
            userPrompt = userPrompt
        )
    }

    private fun languageDisplayName(code: String): String {
        return when (code.lowercase().substringBefore('-')) {
            "vi" -> "Vietnamese"
            "en" -> "English"
            "zh" -> "Chinese"
            "ja" -> "Japanese"
            "ko" -> "Korean"
            "fr" -> "French"
            "de" -> "German"
            "es" -> "Spanish"
            "pt" -> "Portuguese"
            "it" -> "Italian"
            "ru" -> "Russian"
            "ar" -> "Arabic"
            "hi" -> "Hindi"
            "th" -> "Thai"
            "id" -> "Indonesian"
            "ms" -> "Malay"
            "tr" -> "Turkish"
            "nl" -> "Dutch"
            "pl" -> "Polish"
            "sv" -> "Swedish"
            else -> "the language identified by code '$code'"
        }
    }

    private fun buildTranslationSystemPrompt(
        targetLanguageName: String,
        targetLanguageCode: String
    ): String {
        return """
            You translate chat messages into natural $targetLanguageName.
            Preserve meaning, tone, names, emoji, links, and message formatting.
            If the input is already in $targetLanguageName, rewrite it only if needed to sound natural.
            Output rules:
            - Return only the final translated text in $targetLanguageName (code: $targetLanguageCode).
            - Do not add explanations, notes, labels, prefixes, or suffixes.
            - Do not wrap the answer in quotes.
            - Do not use markdown code fences.
            - Do not mention the source language.
        """.trimIndent()
    }

    fun buildSummaryPrompt(summaryMessages: List<String>, roomName: String): PromptSpec {
        val userPrompt = buildString {
            if (roomName.isNotBlank()) {
                append("Chat room: ")
                append(roomName)
                append("\n\n")
            }

            append("Recent messages (oldest to newest):\n")
            summaryMessages.forEachIndexed { index, message ->
                append(index + 1)
                append(". ")
                append(message)
                append('\n')
            }
        }

        return PromptSpec(
            systemPrompt = SUMMARY_SYSTEM_PROMPT,
            userPrompt = userPrompt
        )
    }

    data class PromptSpec(
        val systemPrompt: String,
        val userPrompt: String
    )

    companion object {
        private const val SUMMARY_SYSTEM_PROMPT = """
            You summarize chat conversations in Vietnamese.
            Keep key facts, tasks, decisions, blockers, and follow-up actions.
            Output rules:
            - Return only the summary body, with no intro sentence and no closing sentence.
            - Do not use markdown code fences.
            - Do not add titles like "Tom tat" or "Summary".
            - Use short paragraphs.
            - Use plain bullet lines starting with "-" only when they improve clarity.
            - Do not invent facts that are not present in the messages.
        """
    }
}
