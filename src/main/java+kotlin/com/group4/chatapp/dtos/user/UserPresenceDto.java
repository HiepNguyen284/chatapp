package com.group4.chatapp.dtos.user;

import org.springframework.lang.Nullable;

import java.sql.Timestamp;

public record UserPresenceDto(
    String username,
    boolean online,
    @Nullable Timestamp lastSeenAt
) {}
