package com.group4.chatapp.controllers;

import com.group4.chatapp.dtos.speech.SpeechToTextResponseDto;
import com.group4.chatapp.services.speech.SpeechToTextService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@SecurityRequirements({
    @SecurityRequirement(name = "basicAuth"),
    @SecurityRequirement(name = "bearerAuth")
})
@RequiredArgsConstructor
public class SpeechToTextController {

    private final SpeechToTextService speechToTextService;

    @PostMapping(
        value = {"/api/v1/speech-to-text", "/speech-to-text"},
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public SpeechToTextResponseDto speechToText(
        @RequestParam("audio") MultipartFile audio,
        @RequestParam(name = "language", defaultValue = "vi") String language,
        @RequestParam(name = "prompt", required = false) String prompt
    ) {
        return speechToTextService.transcribe(audio, language, prompt);
    }
}
