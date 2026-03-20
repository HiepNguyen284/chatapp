package com.group4.chatapp.services;

import com.group4.chatapp.dtos.user.UserPresenceDto;
import com.group4.chatapp.exceptions.ApiException;
import com.group4.chatapp.models.User;
import com.group4.chatapp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class PresenceService {

    private final UserRepository userRepository;
    private final SimpUserRegistry simpUserRegistry;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional(readOnly = true)
    public UserPresenceDto getPresence(String username) {
        var user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

        return toPresenceDto(user);
    }

    @Transactional
    public void markConnected(String username) {
        publishPresence(username, true);
    }

    @Transactional
    public void markDisconnected(String username) {
        userRepository.findByUsername(username).ifPresent(user -> {
            user.setLastSeenAt(Timestamp.from(Instant.now()));
            userRepository.save(user);
        });

        publishPresence(username, false);
    }

    private void publishPresence(String username, boolean online) {
        userRepository.findByUsername(username).ifPresent(user ->
            messagingTemplate.convertAndSend("/queue/presence/", toPresenceDto(user, online))
        );
    }

    private UserPresenceDto toPresenceDto(User user) {
        final var isOnline = simpUserRegistry.getUser(user.getUsername()) != null;

        return toPresenceDto(user, isOnline);
    }

    private UserPresenceDto toPresenceDto(User user, boolean online) {

        return new UserPresenceDto(
            user.getUsername(),
            online,
            user.getLastSeenAt()
        );
    }
}
