package com.group4.chatapp.controllers;

import com.group4.chatapp.dtos.messages.MessageReceiveDto;
import com.group4.chatapp.dtos.messages.MessageSendDto;
import com.group4.chatapp.dtos.messages.MessageSendResponseDto;
import com.group4.chatapp.dtos.messages.MessageSummarizeRequestDto;
import com.group4.chatapp.dtos.messages.MessageSummaryDto;
import com.group4.chatapp.dtos.messages.MessageTranslateRequestDto;
import com.group4.chatapp.dtos.messages.MessageTranslationDto;
import com.group4.chatapp.dtos.messages.MessageTypingDto;
import com.group4.chatapp.services.ai.SummaryService;
import com.group4.chatapp.services.ai.TranslationService;
import com.group4.chatapp.services.messages.MessageService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@SecurityRequirements({
    @SecurityRequirement(name = "basicAuth"),
    @SecurityRequirement(name = "bearerAuth")
})
@RequestMapping("/api/v1/messages/")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final TranslationService translationService;
    private final SummaryService summaryService;

    @GetMapping
    public List<MessageReceiveDto> getMessages(
        @RequestParam(name = "room") long roomId,
        @RequestParam(name = "page", defaultValue = "1") int page
    ) {
        return messageService.getMessages(roomId, page);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public MessageSendResponseDto sendMessage(
        @RequestParam(name = "room") long roomId,
        @Valid @ModelAttribute MessageSendDto dto
    ) {
        dto.validate();
        return messageService.sendMessage(roomId, dto);
    }

    @PutMapping(value = "/{messageId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changeMessage(
        @PathVariable long messageId,
        @Valid @ModelAttribute MessageSendDto dto
    ) {
        dto.validate();
        messageService.changeMessage(messageId, dto);
    }

    @DeleteMapping("/{messageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void recallMessage(@PathVariable long messageId) {
        messageService.deleteMessage(messageId);
    }

    @PostMapping("/typing")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setTypingStatus(
        @RequestParam(name = "room") long roomId,
        @RequestBody MessageTypingDto dto
    ) {
        messageService.setTypingStatus(roomId, dto.typing());
    }

    @PostMapping("/read")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void markRoomRead(@RequestParam(name = "room") long roomId) {
        messageService.setReadStatus(roomId);
    }

    @PostMapping(value = "/translate", consumes = MediaType.APPLICATION_JSON_VALUE)
    public MessageTranslationDto translateMessage(
        @Valid @RequestBody MessageTranslateRequestDto dto
    ) {
        return translationService.translate(dto);
    }

    @PostMapping(value = "/summarize", consumes = MediaType.APPLICATION_JSON_VALUE)
    public MessageSummaryDto summarizeMessages(
        @Valid @RequestBody MessageSummarizeRequestDto dto
    ) {
        return summaryService.summarize(dto);
    }
}
