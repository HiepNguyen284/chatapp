package com.group4.chatapp.dtos.messages;

import com.group4.chatapp.dtos.AttachmentDto;
import com.group4.chatapp.dtos.user.UserWithAvatarDto;
import com.group4.chatapp.models.ChatMessage;
import com.group4.chatapp.models.User;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

public record MessageReceiveDto(
    long id,
    String sender,
    String message,
    Timestamp sentOn,
    List<AttachmentDto> attachments,
    List<UserWithAvatarDto> seenBy
) {

    public MessageReceiveDto(ChatMessage message) {

        this(
            message.getId(),
            message.getSender().getUsername(),
            message.getMessage(),
            message.getSentOn(),
            message.getAttachments()
                .stream()
                .filter(Objects::nonNull)
                .map(AttachmentDto::new)
                .toList(),
            List.of()
        );
    }

    public MessageReceiveDto(ChatMessage message, List<User> seenByUsers) {

        this(
            message.getId(),
            message.getSender().getUsername(),
            message.getMessage(),
            message.getSentOn(),
            message.getAttachments()
                .stream()
                .filter(Objects::nonNull)
                .map(AttachmentDto::new)
                .toList(),
            seenByUsers
                .stream()
                .filter(Objects::nonNull)
                .map(UserWithAvatarDto::new)
                .toList()
        );
    }
}
