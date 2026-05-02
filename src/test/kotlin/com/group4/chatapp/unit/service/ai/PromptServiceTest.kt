package com.group4.chatapp.unit.service.ai

import com.group4.chatapp.services.ai.PromptService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertContains
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class PromptServiceTest {

    private val promptService = PromptService()

    @Nested
    @DisplayName("buildTranslationPrompt")
    inner class BuildTranslationPrompt {

        @Test
        fun `should build prompt with target language`() {
            val prompt = promptService.buildTranslationPrompt(
                text = "Hello world",
                sourceLanguage = "en",
                targetLanguage = "vi",
                previousMessages = emptyList()
            )

            assertNotNull(prompt)
            assertContains(prompt.systemPrompt, "Vietnamese")
            assertContains(prompt.userPrompt, "Hello world")
            assertContains(prompt.userPrompt, "Vietnamese")
        }

        @Test
        fun `should include source language hint when not auto`() {
            val prompt = promptService.buildTranslationPrompt(
                text = "Bonjour",
                sourceLanguage = "fr",
                targetLanguage = "en",
                previousMessages = emptyList()
            )

            assertContains(prompt.userPrompt, "Source language hint: fr")
        }

        @Test
        fun `should not include source language hint when auto`() {
            val prompt = promptService.buildTranslationPrompt(
                text = "Hello",
                sourceLanguage = "auto",
                targetLanguage = "vi",
                previousMessages = emptyList()
            )

            assertTrue(!prompt.userPrompt.contains("Source language hint"))
        }

        @Test
        fun `should include conversation context when present`() {
            val prompt = promptService.buildTranslationPrompt(
                text = "Me too",
                sourceLanguage = "en",
                targetLanguage = "vi",
                previousMessages = listOf("Hi there", "How are you?")
            )

            assertContains(prompt.userPrompt, "Conversation context")
            assertContains(prompt.userPrompt, "Hi there")
            assertContains(prompt.userPrompt, "How are you?")
        }

        @Test
        fun `should default empty target language to vi`() {
            val prompt = promptService.buildTranslationPrompt(
                text = "Hello",
                sourceLanguage = "auto",
                targetLanguage = "",
                previousMessages = emptyList()
            )

            assertContains(prompt.systemPrompt, "Vietnamese")
        }
    }

    @Nested
    @DisplayName("buildSummaryPrompt")
    inner class BuildSummaryPrompt {

        @Test
        fun `should build summary prompt with messages`() {
            val prompt = promptService.buildSummaryPrompt(
                summaryMessages = listOf("User1: Hi", "User2: Hello"),
                roomName = "Test Room"
            )

            assertNotNull(prompt)
            assertContains(prompt.userPrompt, "Test Room")
            assertContains(prompt.userPrompt, "User1: Hi")
            assertContains(prompt.systemPrompt, "summarize")
        }

        @Test
        fun `should skip room name when blank`() {
            val prompt = promptService.buildSummaryPrompt(
                summaryMessages = listOf("Message 1"),
                roomName = ""
            )

            assertTrue(!prompt.userPrompt.contains("Chat room:"))
        }
    }
}
