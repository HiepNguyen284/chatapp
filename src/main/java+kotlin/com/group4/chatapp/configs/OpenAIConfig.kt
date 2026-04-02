package com.group4.chatapp.configs

import com.openai.client.OpenAIClient
import com.openai.client.okhttp.OpenAIOkHttpClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
class OpenAIConfig(
    @Value("\${agents.messages.api-key}") private val apiKey: String,
    @Value("\${agents.messages.base-url}") private val baseUrl: String,
    @Value("\${agents.messages.request-timeout-seconds:45}") private val timeout: Int
) {

    @Bean
    fun openAIClient(): OpenAIClient {
        val normalizedBaseUrl = baseUrl.trim()
        check(normalizedBaseUrl.isNotEmpty()) { "Message AI service is not configured" }

        val normalizedApiKey = apiKey.trim()
        check(normalizedApiKey.isNotEmpty()) { "LLM_API_KEY is not configured" }

        return OpenAIOkHttpClient.builder()
            .apiKey(normalizedApiKey)
            .baseUrl(normalizedBaseUrl)
            .timeout(Duration.ofSeconds(maxOf(timeout, 10).toLong()))
            .maxRetries(3)
            .build()
    }
}
