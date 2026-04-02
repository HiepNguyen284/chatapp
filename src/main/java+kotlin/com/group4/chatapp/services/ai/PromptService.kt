package com.group4.chatapp.services.ai

import org.springframework.stereotype.Service

@Service
class PromptService {

    fun buildTranslationPrompt(
        text: String,
        sourceLanguage: String,
        previousMessages: List<String>
    ): PromptSpec {
        val userPrompt = buildString {
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
            systemPrompt = TRANSLATOR_SYSTEM_PROMPT,
            userPrompt = userPrompt
        )
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
        private const val TRANSLATOR_SYSTEM_PROMPT = """
            You translate chat messages into natural Vietnamese.
            Preserve meaning, tone, names, emoji, links, and message formatting.
            If the input is already Vietnamese, rewrite it only if needed to sound natural.
            Output rules:
            - Return only the final Vietnamese text.
            - Do not add explanations, notes, labels, prefixes, or suffixes.
            - Do not wrap the answer in quotes.
            - Do not use markdown code fences.
            - Do not mention the source language.
        """

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
