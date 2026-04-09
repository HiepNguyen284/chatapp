package com.group4.chatapp.services;

import com.group4.chatapp.dtos.AttachmentDto;
import com.group4.chatapp.dtos.ChatRoomDto;
import com.group4.chatapp.exceptions.ApiException;
import com.group4.chatapp.models.ChatRoom;
import com.group4.chatapp.models.ChatRoomPin;
import com.group4.chatapp.repositories.ChatRoomReadStateRepository;
import com.group4.chatapp.repositories.ChatRoomPinRepository;
import com.group4.chatapp.repositories.ChatRoomRepository;
import com.group4.chatapp.repositories.MessageRepository;
import com.group4.chatapp.dtos.messages.VideoCallResponseDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final UserService userService;

    private final MessageRepository messageRepository;
    private final ChatRoomReadStateRepository chatRoomReadStateRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomPinRepository chatRoomPinRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final AgoraTokenService agoraTokenService;
    private final NotificationService notificationService;

    public ChatRoomDto getRoomWithLatestMessage(ChatRoom chatRoom) {

        var latestMessage = messageRepository
            .findFirstByRoom_IdOrderBySentOnDescIdDesc(chatRoom.getId())
            .orElse(null);

        return new ChatRoomDto(chatRoom, latestMessage);
    }

    public List<ChatRoomDto> listRoomsWithLatestMessage() {
        var user = userService.getUserOrThrows();
        var pinnedRoomIds = chatRoomPinRepository.findPinnedRoomIdsByUserId(user.getId())
            .stream()
            .collect(Collectors.toSet());
        return chatRoomRepository.findWithLatestMessage(user.getId())
            .stream()
            .map(room -> {
                var dto = enrichDuoPreview(room, user.getUsername());
                dto.setPinned(pinnedRoomIds.contains(dto.getId()));
                return dto;
            })
            .toList();
    }

    @Transactional
    public void pinRoom(long roomId) {
        var user = userService.getUserOrThrows();
        var room = getRoomAsMemberOrThrow(user.getId(), roomId);

        if (!chatRoomPinRepository.existsByUser_IdAndChatRoom_Id(user.getId(), roomId)) {
            var pin = ChatRoomPin.builder()
                .user(user)
                .chatRoom(room)
                .build();
            chatRoomPinRepository.save(pin);
        }

        publishPinnedState(user.getUsername(), room, true);
    }

    @Transactional
    public void unpinRoom(long roomId) {
        var user = userService.getUserOrThrows();
        var room = getRoomAsMemberOrThrow(user.getId(), roomId);

        chatRoomPinRepository.deleteByUser_IdAndChatRoom_Id(user.getId(), roomId);
        publishPinnedState(user.getUsername(), room, false);
    }

    @Transactional
    public VideoCallResponseDto initiateVideoCall(long roomId) {
        var user = userService.getUserOrThrows();
        var room = getRoomAsMemberOrThrow(user.getId(), roomId);

        var channelName = "room_" + roomId;
        var callerUid = (int) (user.getId() % 100000);

        var callerToken = agoraTokenService.generateToken(channelName, callerUid);

        var senderDisplayName = user.getDisplayName() != null ? user.getDisplayName() : user.getUsername();
        var senderAvatar = user.getAvatar() != null ? user.getAvatar().getSource() : "";

        // Notify ALL other members of the room
        room.getMembers().stream()
            .filter(member -> !member.getId().equals(user.getId()))
            .forEach(member -> {
                var receiverUid = (int) (member.getId() % 100000);
                var receiverToken = agoraTokenService.generateToken(channelName, receiverUid);

                var payload = new java.util.HashMap<String, Object>();
                payload.put("type", "video_call");
                payload.put("roomId", roomId);
                payload.put("channelName", channelName);
                payload.put("agoraToken", receiverToken);
                payload.put("agoraUid", receiverUid);
                payload.put("senderUsername", user.getUsername());
                payload.put("senderDisplayName", senderDisplayName);
                payload.put("senderAvatar", senderAvatar);

                messagingTemplate.convertAndSendToUser(
                    member.getUsername(),
                    "/queue/calls/video",
                    payload
                );

                notificationService.pushVideoCall(
                    member.getUsername(),
                    senderDisplayName,
                    roomId,
                    channelName,
                    receiverToken
                );
            });

        return new VideoCallResponseDto(channelName, callerToken, roomId, callerUid);
    }

    @Transactional
    public void rejectVideoCall(long roomId) {
        var user = userService.getUserOrThrows();
        var room = getRoomAsMemberOrThrow(user.getId(), roomId);

        var rejectorName = user.getDisplayName() != null ? user.getDisplayName() : user.getUsername();

        // Notify everyone else in the room that this user rejected
        var payload = java.util.Map.of(
            "type", "video_call_rejected",
            "roomId", roomId,
            "rejectedBy", rejectorName,
            "rejectedByUsername", user.getUsername()
        );

        room.getMembers().stream()
            .filter(member -> !member.getId().equals(user.getId()))
            .forEach(member ->
                messagingTemplate.convertAndSendToUser(
                    member.getUsername(),
                    "/queue/calls/video_rejected",
                    payload
                )
            );
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

        chatRoomPinRepository.deleteByChatRoom_Id(roomId);
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

    private ChatRoom getRoomAsMemberOrThrow(long userId, long roomId) {
        var room = chatRoomRepository.findById(roomId)
            .orElseThrow(() -> new ApiException(
                HttpStatus.NOT_FOUND,
                "Chat room not found"
            ));

        var isMember = chatRoomRepository.userIsMemberInChatRoom(userId, roomId);
        if (!isMember) {
            throw new ApiException(
                HttpStatus.FORBIDDEN,
                "You are not a member of this room"
            );
        }

        return room;
    }

    private void publishPinnedState(String username, ChatRoom room, boolean pinned) {
        var chatRoomDto = enrichDuoPreview(getRoomWithLatestMessage(room), username);
        chatRoomDto.setPinned(pinned);

        var payload = Map.of(
            "roomId", room.getId(),
            "pinned", pinned,
            "chatRoom", chatRoomDto
        );

        messagingTemplate.convertAndSendToUser(
            username,
            "/queue/chatrooms/pinned/",
            payload
        );
    }
}
