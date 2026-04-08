package com.group4.chatapp.controllers;

import com.group4.chatapp.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/health/")
@RequiredArgsConstructor
public class HealthController {

    private final NotificationService notificationService;

    @GetMapping("firebase/")
    public Map<String, Object> getFirebaseHealth() {
        Map<String, Object> response = new HashMap<>();
        response.put("service", "firebase");
        response.put("enabled", notificationService.isFirebaseEnabled());
        response.put("status", notificationService.isFirebaseEnabled() ? "operational" : "disabled");
        return response;
    }
}
