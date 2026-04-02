package com.group4.chatapp.controllers;

import com.group4.chatapp.dtos.chatbot.ChatbotConversationCreateDto;
import com.group4.chatapp.dtos.chatbot.ChatbotConversationDto;
import com.group4.chatapp.dtos.chatbot.ChatbotMessageDto;
import com.group4.chatapp.dtos.chatbot.ChatbotStreamRequestDto;
import com.group4.chatapp.services.chatbot.ChatbotService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@SecurityRequirements({
    @SecurityRequirement(name = "basicAuth"),
    @SecurityRequirement(name = "bearerAuth")
})
@RequestMapping("/api/v1/chatbot/")
@RequiredArgsConstructor
public class ChatbotController {

    private final ChatbotService chatbotService;

    @GetMapping("/conversations")
    public List<ChatbotConversationDto> listConversations() {
        return chatbotService.listConversations();
    }

    @PostMapping(value = "/conversations", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ChatbotConversationDto createConversation(
        @Valid @RequestBody ChatbotConversationCreateDto dto
    ) {
        return chatbotService.createConversation(dto);
    }

    @GetMapping("/conversations/{conversationId}/messages")
    public List<ChatbotMessageDto> listMessages(@PathVariable long conversationId) {
        return chatbotService.listMessages(conversationId);
    }

    @DeleteMapping("/conversations/{conversationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteConversation(@PathVariable long conversationId) {
        chatbotService.deleteConversation(conversationId);
    }

    @PostMapping(
        value = "/conversations/{conversationId}/stream",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.TEXT_EVENT_STREAM_VALUE
    )
    public SseEmitter streamReply(
        @PathVariable long conversationId,
        @Valid @RequestBody ChatbotStreamRequestDto dto
    ) {
        return chatbotService.streamReply(conversationId, dto);
    }
}
