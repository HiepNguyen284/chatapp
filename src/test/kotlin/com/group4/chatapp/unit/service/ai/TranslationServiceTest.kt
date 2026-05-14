package com.group4.chatapp.unit.service.ai

import com.group4.chatapp.dtos.messages.MessageTranslateRequestDto
import com.group4.chatapp.exceptions.ApiException
import com.group4.chatapp.services.ai.OpenAIClientService
import com.group4.chatapp.services.ai.PromptService
import com.group4.chatapp.services.ai.TranslationService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.springframework.http.HttpStatus
import java.time.Duration
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ExtendWith(MockitoExtension::class)
class TranslationServiceTest {

    @Mock lateinit var openAIClientService: OpenAIClientService
    @Mock lateinit var promptService: PromptService
    @Mock lateinit var redisTemplate: StringRedisTemplate
    @Mock lateinit var valueOps: ValueOperations<String, String>

    @InjectMocks
    lateinit var translationService: TranslationService

    @Nested
    @DisplayName("translate")
    inner class Translate {

        @Test
        fun `should translate text successfully`() {
            val dto = mock<MessageTranslateRequestDto> {
                on { text() } doReturn "Hello"
                on { sourceLanguage() } doReturn "en"
                on { targetLanguage() } doReturn "vi"
                on { previousMessages() } doReturn emptyList()
            }

            whenever(redisTemplate.opsForValue()).thenReturn(valueOps)
            whenever(valueOps.get(any())).thenReturn(null) // cache miss

            val prompt = PromptService.PromptSpec("system", "user")
            whenever(promptService.buildTranslationPrompt(any(), any(), any(), any())).thenReturn(prompt)
            whenever(openAIClientService.requestText(prompt, "Translation service", 0.1))
                .thenReturn("Xin chào")

            val result = translationService.translate(dto)

            assertNotNull(result)
            assertEquals("Xin chào", result.translatedText())
            assertEquals("vi", result.targetLanguage())
        }

        @Test
        fun `should return cached translation on Redis hit`() {
            val dto = mock<MessageTranslateRequestDto> {
                on { text() } doReturn "Hello"
                on { sourceLanguage() } doReturn "en"
                on { targetLanguage() } doReturn "vi"
                on { previousMessages() } doReturn emptyList()
            }

            whenever(redisTemplate.opsForValue()).thenReturn(valueOps)
            whenever(valueOps.get(any())).thenReturn("Xin chào (cached)")

            val result = translationService.translate(dto)

            assertNotNull(result)
            assertEquals("Xin chào (cached)", result.translatedText())
            verify(openAIClientService, never()).requestText(any(), any(), any())
        }

        @Test
        fun `should throw when text is empty`() {
            val dto = mock<MessageTranslateRequestDto> {
                on { text() } doReturn ""
            }

            org.junit.jupiter.api.assertThrows<ApiException> {
                translationService.translate(dto)
            }
        }

        @Test
        fun `should throw when AI is rate limited`() {
            val dto = mock<MessageTranslateRequestDto> {
                on { text() } doReturn "Hello"
                on { sourceLanguage() } doReturn "en"
                on { targetLanguage() } doReturn "vi"
                on { previousMessages() } doReturn emptyList()
            }

            whenever(redisTemplate.opsForValue()).thenReturn(valueOps)
            whenever(valueOps.get(any())).thenReturn(null)

            val prompt = PromptService.PromptSpec("system", "user")
            whenever(promptService.buildTranslationPrompt(any(), any(), any(), any())).thenReturn(prompt)
            whenever(openAIClientService.requestText(prompt, "Translation service", 0.1))
                .thenThrow(ApiException(HttpStatus.TOO_MANY_REQUESTS, "Rate limited"))

            org.junit.jupiter.api.assertThrows<ApiException> {
                translationService.translate(dto)
            }
        }

        @Test
        fun `should throw when AI service is unavailable`() {
            val dto = mock<MessageTranslateRequestDto> {
                on { text() } doReturn "Hello"
                on { sourceLanguage() } doReturn "en"
                on { targetLanguage() } doReturn "vi"
                on { previousMessages() } doReturn emptyList()
            }

            whenever(redisTemplate.opsForValue()).thenReturn(valueOps)
            whenever(valueOps.get(any())).thenReturn(null)

            val prompt = PromptService.PromptSpec("system", "user")
            whenever(promptService.buildTranslationPrompt(any(), any(), any(), any())).thenReturn(prompt)
            whenever(openAIClientService.requestText(prompt, "Translation service", 0.1))
                .thenThrow(ApiException(HttpStatus.BAD_GATEWAY, "Service unavailable"))

            org.junit.jupiter.api.assertThrows<ApiException> {
                translationService.translate(dto)
            }
        }

        @Test
        fun `should default target language to vi when empty`() {
            val dto = mock<MessageTranslateRequestDto> {
                on { text() } doReturn "Hello"
                on { sourceLanguage() } doReturn "en"
                on { targetLanguage() } doReturn ""
                on { previousMessages() } doReturn emptyList()
            }

            whenever(redisTemplate.opsForValue()).thenReturn(valueOps)
            whenever(valueOps.get(any())).thenReturn(null)

            val prompt = PromptService.PromptSpec("system", "user")
            whenever(promptService.buildTranslationPrompt(eq("Hello"), eq("en"), eq("vi"), any())).thenReturn(prompt)
            whenever(openAIClientService.requestText(prompt, "Translation service", 0.1))
                .thenReturn("Xin chào")

            val result = translationService.translate(dto)

            assertEquals("vi", result.targetLanguage())
        }

        @Test
        fun `should cache translation after successful API call`() {
            val dto = mock<MessageTranslateRequestDto> {
                on { text() } doReturn "Hello"
                on { sourceLanguage() } doReturn "en"
                on { targetLanguage() } doReturn "vi"
                on { previousMessages() } doReturn emptyList()
            }

            whenever(redisTemplate.opsForValue()).thenReturn(valueOps)
            whenever(valueOps.get(any())).thenReturn(null)

            val prompt = PromptService.PromptSpec("system", "user")
            whenever(promptService.buildTranslationPrompt(any(), any(), any(), any())).thenReturn(prompt)
            whenever(openAIClientService.requestText(prompt, "Translation service", 0.1))
                .thenReturn("Xin chào")

            translationService.translate(dto)

            verify(valueOps).set(any(), eq("Xin chào"), eq(Duration.ofHours(24)))
        }
    }
}
