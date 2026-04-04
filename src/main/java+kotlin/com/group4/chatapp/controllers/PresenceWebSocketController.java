package com.group4.chatapp.controllers;

import com.group4.chatapp.services.PresenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class PresenceWebSocketController {

    private final PresenceService presenceService;

    @MessageMapping("/app-presence")
    public void handleAppPresence(@Payload Map<String, Object> payload, Principal principal) {
        if (principal == null) return;

        String username = principal.getName();
        Object active = payload.get("active");
        if (active instanceof Boolean) {
            presenceService.markAppActive(username, (Boolean) active);
        }
    }
}
