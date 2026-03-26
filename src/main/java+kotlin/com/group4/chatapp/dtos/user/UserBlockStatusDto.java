package com.group4.chatapp.dtos.user;

public record UserBlockStatusDto(
    String username,
    boolean blockedByMe,
    boolean blockedByUser
) {
}
