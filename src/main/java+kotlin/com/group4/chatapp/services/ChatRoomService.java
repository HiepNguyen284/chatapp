package com.group4.chatapp.services;

import com.group4.chatapp.dtos.AttachmentDto;
import com.group4.chatapp.dtos.ChatRoomDto;
import com.group4.chatapp.exceptions.ApiException;
import com.group4.chatapp.models.ChatRoom;
import com.group4.chatapp.repositories.ChatRoomReadStateRepository;
import com.group4.chatapp.repositories.ChatRoomRepository;
import com.group4.chatapp.repositories.MessageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final UserService userService;

    private final MessageRepository messageRepository;
    private final ChatRoomReadStateRepository chatRoomReadStateRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatRoomDto getRoomWithLatestMessage(ChatRoom chatRoom) {

        var latestMessage = messageRepository
            .findFirstByRoom_IdOrderBySentOnDescIdDesc(chatRoom.getId())
            .orElse(null);

        return new ChatRoomDto(chatRoom, latestMessage);
    }

    public List<ChatRoomDto> listRoomsWithLatestMessage() {
        var user = userService.getUserOrThrows();
        return chatRoomRepository.findWithLatestMessage(user.getId())
            .stream()
            .map(room -> enrichDuoPreview(room, user.getUsername()))
            .toList();
    }

    @Transactional
    public void removeFriend(long roomId) {
        var user = userService.getUserOrThrows();

        var room = chatRoomRepository.findById(roomId)
            .orElseThrow(() -> new ApiException(
                HttpStatus.NOT_FOUND,
                "Chat room not found"
            ));

        if (room.getType() != ChatRoom.Type.DUO) {
            throw new ApiException(
                HttpStatus.BAD_REQUEST,
                "Only duo rooms can be removed as friend"
            );
        }

        var isMember = room.getMembers().stream()
            .anyMatch(member -> member.getId().equals(user.getId()));

        if (!isMember) {
            throw new ApiException(
                HttpStatus.FORBIDDEN,
                "You are not a member of this room"
            );
        }

        chatRoomReadStateRepository.deleteByRoomId(roomId);
        messageRepository.deleteByRoomId(roomId);
        chatRoomRepository.delete(room);

        var payload = Map.of("roomId", roomId);

        room.getMembers().forEach(member -> 
            messagingTemplate.convertAndSendToUser(
                member.getUsername(),
                "/queue/friends/removed/",
                payload
            )
        );
    }

    private ChatRoomDto enrichDuoPreview(ChatRoomDto room, String currentUsername) {

        if (room.getType() != ChatRoom.Type.DUO) {
            return room;
        }

        var peerUsername = room.getMembersUsername()
            .stream()
            .filter(username -> !username.equals(currentUsername))
            .findFirst()
            .orElse(null);

        if (peerUsername == null || peerUsername.isBlank()) {
            return room;
        }

        userService.getUserByUsername(peerUsername).ifPresent(peer -> {
            room.setName(peer.getDisplayName() == null || peer.getDisplayName().isBlank()
                ? peer.getUsername()
                : peer.getDisplayName());

            if (peer.getAvatar() == null) {
                room.setAvatar(null);
            } else {
                room.setAvatar(new AttachmentDto(peer.getAvatar()));
            }
        });

        return room;
    }
}
