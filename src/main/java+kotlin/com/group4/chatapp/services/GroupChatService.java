package com.group4.chatapp.services;

import com.group4.chatapp.dtos.ChatRoomDto;
import com.group4.chatapp.dtos.group.GroupChatCreateDto;
import com.group4.chatapp.dtos.group.GroupChatDto;
import com.group4.chatapp.dtos.group.GroupChatUpdateDto;
import com.group4.chatapp.dtos.user.UserWithAvatarDto;
import com.group4.chatapp.exceptions.ApiException;
import com.group4.chatapp.models.Attachment;
import com.group4.chatapp.models.ChatRoom;
import com.group4.chatapp.models.ChatRoomMember;
import com.group4.chatapp.models.User;
import com.group4.chatapp.repositories.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupChatService {

    private final UserService userService;
    private final AttachmentService attachmentService;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final MessageRepository messageRepository;
    private final ChatRoomReadStateRepository chatRoomReadStateRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public GroupChatDto createGroup(GroupChatCreateDto dto) {
        var creator = userService.getUserOrThrows();

        // Validate member IDs
        var members = new HashSet<User>();
        members.add(creator);

        for (Long memberId : dto.memberIds()) {
            var user = userService.getUserById(memberId)
                .orElseThrow(() -> new ApiException(
                    HttpStatus.NOT_FOUND,
                    "User not found: " + memberId
                ));
            members.add(user);
        }

        // Validate and set avatar if provided
        Attachment avatar = null;
        if (dto.avatarId() != null) {
            avatar = attachmentService.getAttachmentOrThrow(dto.avatarId());
            if (!avatar.isImage()) {
                throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "Avatar must be an image"
                );
            }
        }

        // Create the chat room
        var room = ChatRoom.builder()
            .name(dto.name())
            .avatar(avatar)
            .members(members)
            .type(ChatRoom.Type.GROUP)
            .build();

        room = chatRoomRepository.save(room);

        // Create member records - creator is admin
        for (User member : members) {
            var isAdmin = member.getId().equals(creator.getId());
            var chatRoomMember = ChatRoomMember.createMember(room, member, isAdmin);
            chatRoomMemberRepository.save(chatRoomMember);
        }

        publishRoomCreated(room, members);

        // Return the created group
        return buildGroupChatDto(room, creator.getId());
    }

    @Transactional
    public GroupChatDto updateGroup(long roomId, GroupChatUpdateDto dto) {
        var user = userService.getUserOrThrows();
        var room = getGroupRoomOrThrow(roomId);

        // Check if user is admin
        if (!chatRoomMemberRepository.isUserAdmin(roomId, user.getId())) {
            throw new ApiException(
                HttpStatus.FORBIDDEN,
                "Only admin can update group"
            );
        }

        // Update name if provided
        if (dto.name() != null && !dto.name().isBlank()) {
            room.setName(dto.name());
        }

        // Update avatar if provided
        if (dto.avatarId() != null) {
            var avatar = attachmentService.getAttachmentOrThrow(dto.avatarId());
            if (!avatar.isImage()) {
                throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "Avatar must be an image"
                );
            }
            room.setAvatar(avatar);
        }

        room = chatRoomRepository.save(room);

        // Notify members about update
        notifyMembers(roomId, "group_updated", Map.of("roomId", roomId));

        return buildGroupChatDto(room, user.getId());
    }

    @Transactional
    public GroupChatDto addMembers(long roomId, List<Long> memberIds) {
        var user = userService.getUserOrThrows();
        var room = getGroupRoomOrThrow(roomId);

        // Check if user is admin
        if (!chatRoomMemberRepository.isUserAdmin(roomId, user.getId())) {
            throw new ApiException(
                HttpStatus.FORBIDDEN,
                "Only admin can add members"
            );
        }

        // Get current members
        var currentMembers = room.getMembers();
        var newMembers = new HashSet<User>();

        for (Long memberId : memberIds) {
            var targetUser = userService.getUserById(memberId)
                .orElseThrow(() -> new ApiException(
                    HttpStatus.NOT_FOUND,
                    "User not found: " + memberId
                ));

            // Check if already a member
            if (currentMembers.contains(targetUser)) {
                continue;
            }

            newMembers.add(targetUser);
        }

        // Add new members to the room
        for (User member : newMembers) {
            room.getMembers().add(member);

            var chatRoomMember = ChatRoomMember.createMember(room, member, false);
            chatRoomMemberRepository.save(chatRoomMember);
        }

        room = chatRoomRepository.save(room);

        publishRoomCreated(room, newMembers);

        // Notify members about new members
        var newMemberUsernames = newMembers.stream()
            .map(User::getUsername)
            .collect(Collectors.toList());
        notifyMembers(roomId, "members_added", Map.of(
            "roomId", roomId,
            "newMembers", newMemberUsernames
        ));

        return buildGroupChatDto(room, user.getId());
    }

    @Transactional
    public void removeMember(long roomId, long targetUserId) {
        var user = userService.getUserOrThrows();
        var room = getGroupRoomOrThrow(roomId);

        // Check if user is admin or is removing themselves
        boolean isAdmin = chatRoomMemberRepository.isUserAdmin(roomId, user.getId());
        boolean isSelf = user.getId().equals(targetUserId);

        if (!isAdmin && !isSelf) {
            throw new ApiException(
                HttpStatus.FORBIDDEN,
                "Only admin can remove other members"
            );
        }

        // Cannot remove yourself if you're the only admin
        if (isSelf && isAdmin) {
            var adminsCount = room.getMembers().stream()
                .filter(m -> chatRoomMemberRepository.isUserAdmin(roomId, m.getId()))
                .count();

            if (adminsCount == 1) {
                throw new ApiException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot leave group - you are the only admin"
                );
            }
        }

        // Remove the member
        var targetUser = userService.getUserById(targetUserId)
            .orElseThrow(() -> new ApiException(
                HttpStatus.NOT_FOUND,
                "User not found"
            ));

        room.getMembers().remove(targetUser);
        chatRoomMemberRepository.deleteByChatRoomIdAndUserId(roomId, targetUserId);

        // Delete read state for removed user
        chatRoomReadStateRepository.deleteByRoomIdAndReaderId(roomId, targetUserId);

        chatRoomRepository.save(room);

        // Notify remaining members
        notifyMembers(roomId, "member_removed", Map.of(
            "roomId", roomId,
            "removedUserId", targetUserId
        ));
    }

    @Transactional
    public void leaveGroup(long roomId) {
        removeMember(roomId, userService.getUserOrThrows().getId());
    }

    public GroupChatDto getGroupDetails(long roomId) {
        var user = userService.getUserOrThrows();
        var room = getGroupRoomOrThrow(roomId);

        // Check if user is a member
        if (!chatRoomMemberRepository.isUserMember(roomId, user.getId())) {
            throw new ApiException(
                HttpStatus.FORBIDDEN,
                "You are not a member of this group"
            );
        }

        return buildGroupChatDto(room, user.getId());
    }

    private ChatRoom getGroupRoomOrThrow(long roomId) {
        var room = chatRoomRepository.findById(roomId)
            .orElseThrow(() -> new ApiException(
                HttpStatus.NOT_FOUND,
                "Chat room not found"
            ));

        if (room.getType() != ChatRoom.Type.GROUP) {
            throw new ApiException(
                HttpStatus.BAD_REQUEST,
                "This is not a group chat"
            );
        }

        return room;
    }

    private GroupChatDto buildGroupChatDto(ChatRoom room, long currentUserId) {
        var latestMessage = messageRepository.findLatestMessage(room.getId())
            .orElse(null);

        var members = room.getMembers().stream()
            .map(UserWithAvatarDto::new)
            .collect(Collectors.toList());

        boolean isAdmin = chatRoomMemberRepository.isUserAdmin(room.getId(), currentUserId);

        return new GroupChatDto(room, latestMessage, members, isAdmin);
    }

    private void notifyMembers(long roomId, String event, Map<String, Object> payload) {
        var room = chatRoomRepository.findById(roomId).orElse(null);
        if (room == null) return;

        room.getMembers().forEach(member ->
            messagingTemplate.convertAndSendToUser(
                member.getUsername(),
                "/queue/groups/" + event,
                payload
            )
        );
    }

    private void publishRoomCreated(ChatRoom room, Set<User> targets) {
        if (targets.isEmpty()) {
            return;
        }

        var chatRoomDto = new ChatRoomDto(room, null);
        var payload = Map.of("chatRoom", chatRoomDto);

        targets.forEach(member ->
            messagingTemplate.convertAndSendToUser(
                member.getUsername(),
                "/queue/chatrooms/created/",
                payload
            )
        );
    }
}
