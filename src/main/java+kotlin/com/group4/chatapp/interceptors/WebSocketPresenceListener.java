package com.group4.chatapp.interceptors;

import com.group4.chatapp.services.PresenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
public class WebSocketPresenceListener {

    private final PresenceService presenceService;

    @EventListener
    public void onConnect(SessionConnectEvent event) {
        final var accessor = StompHeaderAccessor.wrap(event.getMessage());
        final var user = accessor.getUser();

        if (user == null || user.getName() == null || user.getName().isBlank()) {
            return;
        }

        presenceService.markConnected(user.getName());
    }

    @EventListener
    public void onDisconnect(SessionDisconnectEvent event) {
        final var accessor = StompHeaderAccessor.wrap(event.getMessage());
        final var user = accessor.getUser();

        if (user == null || user.getName() == null || user.getName().isBlank()) {
            return;
        }

        presenceService.markDisconnected(user.getName());
    }
}
