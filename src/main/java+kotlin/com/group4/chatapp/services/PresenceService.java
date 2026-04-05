package com.group4.chatapp.services;

import com.group4.chatapp.dtos.user.UserPresenceDto;
import com.group4.chatapp.exceptions.ApiException;
import com.group4.chatapp.models.User;
import com.group4.chatapp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PresenceService {

    private static final String APP_ACTIVE_KEY_PREFIX = "presence:app_active:";
    private static final String LAST_SEEN_KEY_PREFIX = "presence:last_seen:";

    private final UserRepository userRepository;
    private final SimpUserRegistry simpUserRegistry;
    private final SimpMessagingTemplate messagingTemplate;
    private final StringRedisTemplate redisTemplate;

    @Transactional(readOnly = true)
    public UserPresenceDto getPresence(String username) {
        userRepository.findByUsername(username)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

        return toPresenceDto(username);
    }

    @Transactional
    public void markConnected(String username) {
        publishPresence(username, true);
    }

    @Transactional
    public void markDisconnected(String username) {
        redisTemplate.opsForValue().set(
            LAST_SEEN_KEY_PREFIX + username,
            String.valueOf(System.currentTimeMillis())
        );
        redisTemplate.delete(APP_ACTIVE_KEY_PREFIX + username);
        publishPresence(username, false);
    }

    public void markAppActive(String username, boolean active) {
        String key = APP_ACTIVE_KEY_PREFIX + username;
        if (active) {
            redisTemplate.opsForValue().set(key, "true");
        } else {
            redisTemplate.delete(key);
        }
    }

    public boolean isAppActive(String username) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(APP_ACTIVE_KEY_PREFIX + username));
    }

    private void publishPresence(String username, boolean online) {
        userRepository.findByUsername(username).ifPresent(user ->
            messagingTemplate.convertAndSend("/queue/presence/", toPresenceDto(username))
        );
    }

    private UserPresenceDto toPresenceDto(String username) {
        final var isOnline = simpUserRegistry.getUser(username) != null;

        var values = redisTemplate.opsForValue().multiGet(List.of(
            APP_ACTIVE_KEY_PREFIX + username,
            LAST_SEEN_KEY_PREFIX + username
        ));

        Timestamp lastSeenAt = null;
        if (values != null && values.get(1) != null) {
            lastSeenAt = new Timestamp(Long.parseLong(values.get(1)));
        }

        return new UserPresenceDto(username, isOnline, lastSeenAt);
    }

    public boolean isOnline(String username) {
        return simpUserRegistry.getUser(username) != null;
    }

    public Map<String, UserPresenceDto> getPresenceBatch(List<String> usernames) {
        List<String> allKeys = new java.util.ArrayList<>(usernames.size() * 2);
        for (String u : usernames) {
            allKeys.add(APP_ACTIVE_KEY_PREFIX + u);
            allKeys.add(LAST_SEEN_KEY_PREFIX + u);
        }

        List<String> values = redisTemplate.opsForValue().multiGet(allKeys);

        Map<String, UserPresenceDto> result = new java.util.LinkedHashMap<>();
        for (int i = 0; i < usernames.size(); i++) {
            String username = usernames.get(i);
            boolean isOnline = simpUserRegistry.getUser(username) != null;
            Timestamp lastSeenAt = null;
            if (values != null && values.get(i * 2 + 1) != null) {
                lastSeenAt = new Timestamp(Long.parseLong(values.get(i * 2 + 1)));
            }
            result.put(username, new UserPresenceDto(username, isOnline, lastSeenAt));
        }
        return result;
    }
}
