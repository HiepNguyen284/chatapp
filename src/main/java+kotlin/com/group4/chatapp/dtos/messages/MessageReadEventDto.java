package com.group4.chatapp.dtos.messages;

import com.group4.chatapp.dtos.user.UserWithAvatarDto;

import java.sql.Timestamp;

public record MessageReadEventDto(
    long roomId,
    UserWithAvatarDto reader,
    Timestamp readAt
) {}
