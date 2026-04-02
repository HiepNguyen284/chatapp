package com.group4.chatapp.services.ai

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.group4.chatapp.exceptions.ApiException
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import java.time.Duration

@Service
class OpenAIClientService(
    @Value("\${agents.messages.base-url:}") private val llmBaseUrl: String,
    @Value("\${agents.messages.model:}") private val llmModel: String,
    @Value("\${agents.messages.api-key:}") private val llmApiKey: String,
    @Value("\${agents.messages.request-timeout-seconds:45}") private val llmRequestTimeoutSeconds: Int
) {

    companion object {
        private val httpClient: HttpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build()
    }

    private val objectMapper = ObjectMapper()

    fun requestText(
        prompt: PromptService.PromptSpec,
        serviceName: String,
        temperature: Double
    ): String {
        val request = HttpRequest.newBuilder(buildCompletionsUri())
            .timeout(resolveRequestTimeout())
            .header("Accept", "application/json")
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer ${requireApiKey()}")
            .POST(
                HttpRequest.BodyPublishers.ofString(
                    buildRequestBody(prompt, temperature),
                    StandardCharsets.UTF_8
                )
            )
            .build()

        val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8))
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw ApiException(HttpStatus.BAD_GATEWAY, "$serviceName request failed: ${response.statusCode()}")
        }

        val root = objectMapper.readTree(response.body())
        val result = root.path("choices")
            .asSequence()
            .map { extractMessageText(it.path("message").path("content")) }
            .firstOrNull { it.isNotBlank() }
            .orEmpty()
            .trim()

        if (result.isEmpty()) {
            throw ApiException(
                HttpStatus.BAD_GATEWAY,
                "$serviceName returned an invalid response",
            )
        }

        return result
    }

    private fun buildCompletionsUri(): URI {
        val baseUrl = llmBaseUrl.trim()
        if (baseUrl.isEmpty()) {
            throw ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Message AI service is not configured")
        }

        val separator = if (baseUrl.endsWith("/")) "" else "/"
        return URI.create(baseUrl + separator + "chat/completions")
    }

    private fun buildRequestBody(prompt: PromptService.PromptSpec, temperature: Double): String {
        val root = objectMapper.createObjectNode()
        root.put("model", requireModel())
        root.put("temperature", temperature)

        val messages = root.putArray("messages")
        messages.addObject()
            .put("role", "developer")
            .put("content", prompt.systemPrompt)
        messages.addObject()
            .put("role", "user")
            .put("content", prompt.userPrompt)

        return objectMapper.writeValueAsString(root)
    }

    private fun requireApiKey(): String {
        val apiKey = llmApiKey.trim()
        if (apiKey.isEmpty()) {
            throw ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "LLM_API_KEY is not configured")
        }

        return apiKey
    }

    private fun requireModel(): String {
        val model = llmModel.trim()
        if (model.isEmpty()) {
            throw ApiException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Message AI model is not configured"
            )
        }

        return model
    }

    private fun resolveRequestTimeout(): Duration {
        return Duration.ofSeconds(maxOf(llmRequestTimeoutSeconds, 10).toLong())
    }

    private fun extractMessageText(contentNode: JsonNode?): String {
        if (contentNode == null || contentNode.isNull) {
            return ""
        }

        if (contentNode.isTextual) {
            return contentNode.asText("")
        }

        if (!contentNode.isArray) {
            return ""
        }

        val text = StringBuilder()
        for (segment in contentNode) {
            if (segment == null || segment.isNull) {
                continue
            }

            if (segment.isTextual) {
                text.append(segment.asText(""))
                continue
            }

            if (segment.path("type").asText("").equals("text", ignoreCase = true)) {
                text.append(segment.path("text").asText(""))
            }
        }

        return text.toString()
    }
}
