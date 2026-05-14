package com.group4.chatapp.unit.service.ai

import com.group4.chatapp.dtos.messages.MessageSummarizeRequestDto
import com.group4.chatapp.exceptions.ApiException
import com.group4.chatapp.services.ai.OpenAIClientService
import com.group4.chatapp.services.ai.PromptService
import com.group4.chatapp.services.ai.SummaryService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import org.springframework.http.HttpStatus
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ExtendWith(MockitoExtension::class)
class SummaryServiceTest {

    @Mock lateinit var openAIClientService: OpenAIClientService
    @Mock lateinit var promptService: PromptService

    @InjectMocks
    lateinit var summaryService: SummaryService

    @Nested
    @DisplayName("summarize")
    inner class Summarize {

        @Test
        fun `should return summary from AI`() {
            val dto = mock<MessageSummarizeRequestDto> {
                on { messages() } doReturn listOf("User1: Hello", "User2: Hi there")
                on { roomName() } doReturn "Test Room"
            }

            val prompt = PromptService.PromptSpec("system", "user")
            whenever(promptService.buildSummaryPrompt(any(), any())).thenReturn(prompt)
            whenever(openAIClientService.requestText(prompt, "Summary service", 0.2))
                .thenReturn("This is a summary of the conversation.")

            val result = summaryService.summarize(dto)

            assertNotNull(result)
            assertEquals("This is a summary of the conversation.", result.summary())
            assertEquals(2, result.messageCount())
        }

        @Test
        fun `should return fallback when AI fails`() {
            val dto = mock<MessageSummarizeRequestDto> {
                on { messages() } doReturn listOf("Msg1", "Msg2", "Msg3")
                on { roomName() } doReturn "Room"
            }

            val prompt = PromptService.PromptSpec("system", "user")
            whenever(promptService.buildSummaryPrompt(any(), any())).thenReturn(prompt)
            whenever(openAIClientService.requestText(prompt, "Summary service", 0.2))
                .thenThrow(ApiException(HttpStatus.BAD_GATEWAY, "API failed"))

            val result = summaryService.summarize(dto)

            assertNotNull(result)
            assertContains(result.summary(), "temporarily unavailable")
        }

        @Test
        fun `should throw when messages are empty`() {
            val dto = mock<MessageSummarizeRequestDto> {
                on { messages() } doReturn emptyList()
            }

            org.junit.jupiter.api.assertThrows<ApiException> {
                summaryService.summarize(dto)
            }
        }

        @Test
        fun `should throw when messages are null`() {
            val dto = mock<MessageSummarizeRequestDto> {
                on { messages() } doReturn null
            }

            org.junit.jupiter.api.assertThrows<ApiException> {
                summaryService.summarize(dto)
            }
        }

        @Test
        fun `should use cache on second call with same input`() {
            val dto = mock<MessageSummarizeRequestDto> {
                on { messages() } doReturn listOf("Hello")
                on { roomName() } doReturn "Room"
            }

            val prompt = PromptService.PromptSpec("system", "user")
            whenever(promptService.buildSummaryPrompt(any(), any())).thenReturn(prompt)
            whenever(openAIClientService.requestText(prompt, "Summary service", 0.2))
                .thenReturn("Cached summary")

            // First call
            summaryService.summarize(dto)
            // Second call should use cache
            summaryService.summarize(dto)

            verify(openAIClientService, times(1)).requestText(any(), any(), any())
        }
    }
}
