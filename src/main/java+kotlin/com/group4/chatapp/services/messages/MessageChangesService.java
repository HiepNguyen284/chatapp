package com.group4.chatapp.services.messages;

import com.group4.chatapp.dtos.messages.MessageReceiveDto;
import com.group4.chatapp.dtos.messages.MessageSendDto;
import com.group4.chatapp.dtos.messages.MessageSendResponseDto;
import com.group4.chatapp.exceptions.ApiException;
import com.group4.chatapp.models.ChatMessage;
import com.group4.chatapp.models.ChatRoom;
import com.group4.chatapp.models.User;
import com.group4.chatapp.repositories.MessageRepository;
import com.group4.chatapp.services.AttachmentService;
import com.group4.chatapp.services.NotificationService;
import com.group4.chatapp.services.PresenceService;
import com.group4.chatapp.services.UserBlockService;
import com.group4.chatapp.services.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.jspecify.annotations.Nullable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
class MessageChangesService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageChangesService.class);

    private final MessageRepository messageRepository;

    private final UserService userService;
    private final UserBlockService userBlockService;
    private final MessageCheckService checkService;
    private final AttachmentService attachmentService;
    private final NotificationService notificationService;
    private final PresenceService presenceService;

    private final SimpMessagingTemplate messagingTemplate;

    private void sendToMembers(ChatRoom chatRoom, ChatMessage savedMessage) {

        var socketPath = chatRoom.getSocketPath();
        var messageReceiveDto = new MessageReceiveDto(savedMessage);

        var sender = savedMessage.getSender();
        var senderDisplayName = sender.getDisplayName() != null ? sender.getDisplayName() : sender.getUsername();
        var messageText = savedMessage.getMessage();
        var preview = messageText != null && messageText.length() > 100
            ? messageText.substring(0, 100) + "..."
            : messageText;
        var roomId = chatRoom.getId();

        chatRoom.getMembers()
            .parallelStream()
            .forEach((member) -> {
                try {
                    messagingTemplate.convertAndSendToUser(
                        member.getUsername(),
                        socketPath,
                        messageReceiveDto
                    );

                    if (!member.equals(sender)) {
                        if (presenceService.isOnline(member.getUsername())) {
                            return;
                        }
                        var memberDisplayName = member.getDisplayName() != null ? member.getDisplayName() : member.getUsername();
                        try {
                            notificationService.pushNewMessage(
                                member.getUsername(),
                                senderDisplayName,
                                preview != null ? preview : "New message",
                                roomId
                            );
                        } catch (Exception e) {
                            LOGGER.warn("Failed to send push notification to user {}: {}", member.getUsername(), e.getMessage());
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error("Error sending message to user {}: {}", member.getUsername(), e.getMessage(), e);
                }
            });
    }

    @Nullable
    private ChatMessage getReplyToAndCheck(ChatRoom chatRoom, MessageSendDto dto) {

        var replyToId = dto.getReplyTo();
        if (replyToId == null) {
            return null;
        }

        var message = messageRepository.findById(replyToId)
            .orElseThrow(() -> new ApiException(
                HttpStatus.NOT_FOUND,
                "The reply to message not found!"
            ));

        var fromOtherRoom = !message.getRoom().equals(chatRoom);
        if (fromOtherRoom) {
            throw new ApiException(
                HttpStatus.FORBIDDEN,
                "Can't reply to message from other room."
            );
        }

        return message;
    }

    private ChatMessage saveMessage(User user, ChatRoom chatRoom, MessageSendDto dto) {

        var attachments = attachmentService.getAttachments(dto);

        var newMessage = dto.toMessage(
            getReplyToAndCheck(chatRoom, dto),
            chatRoom, user, attachments,
            ChatMessage.Status.NORMAL
        );

        return messageRepository.save(newMessage);
    }

    @Transactional
    public MessageSendResponseDto sendMessage(long roomId, MessageSendDto dto) {

        var user = userService.getUserOrThrows();
        var chatRoom = checkService.receiveChatRoomAndCheck(roomId, user);
        ensureRoomMessagingAllowed(chatRoom, user);

        var savedMessage = saveMessage(user, chatRoom, dto);
        sendToMembers(chatRoom, savedMessage);

        return new MessageSendResponseDto(savedMessage.getId());
    }

    @Transactional
    public void editMessage(long messageId, MessageSendDto dto) {

        var message = checkService.getMessageAndCheckSender(messageId);
        if (message.isRecalled()) {
            throw new ApiException(
                HttpStatus.CONFLICT,
                "Can't edit recalled message."
            );
        }

        var attachments = attachmentService.getAttachments(dto);

        var chatRoom = message.getRoom();
        var sender = message.getSender();
        ensureRoomMessagingAllowed(chatRoom, sender);
        var now = new Timestamp(System.currentTimeMillis());

        var newMessage = dto.toMessage(
            getReplyToAndCheck(chatRoom, dto),
            chatRoom, sender, attachments,
            ChatMessage.Status.EDITED
        );

        newMessage.setId(message.getId());
        newMessage.setLastEdit(now);

        var saved = messageRepository.save(newMessage);
        sendToMembers(chatRoom, saved);

        // TODO: delete old resources
    }

    @Transactional
    public void recallMessage(long messageId) {

        var message = checkService.getMessageAndCheckSender(messageId);
        var now = new Timestamp(System.currentTimeMillis());

        message.setMessage(null);
        message.setLastEdit(now);
        message.setAttachments(new ArrayList<>());
        message.setStatus(ChatMessage.Status.RECALLED);

        var saved = messageRepository.save(message);
        sendToMembers(saved.getRoom(), saved);
    }

    private void ensureRoomMessagingAllowed(ChatRoom room, User sender) {
        if (room.getType() != ChatRoom.Type.DUO) {
            return;
        }

        var peer = room.getMembers()
            .stream()
            .filter(member -> !member.equals(sender))
            .findFirst()
            .orElse(null);

        if (peer == null) {
            return;
        }

        userBlockService.ensureNotBlockedEitherWay(
            sender,
            peer,
            "Cannot send messages because one user has blocked the other"
        );
    }
}
