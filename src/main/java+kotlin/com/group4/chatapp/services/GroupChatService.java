package com.group4.chatapp.services;

import com.group4.chatapp.dtos.ChatRoomDto;
import com.group4.chatapp.dtos.messages.MessageReceiveDto;
import com.group4.chatapp.dtos.group.GroupChatCreateDto;
import com.group4.chatapp.dtos.group.GroupChatDto;
import com.group4.chatapp.dtos.group.GroupChatUpdateDto;
import com.group4.chatapp.dtos.group.GroupChatUpdateMultipartDto;
import com.group4.chatapp.dtos.user.UserWithAvatarDto;
import com.group4.chatapp.exceptions.ApiException;
import com.group4.chatapp.models.Attachment;
import com.group4.chatapp.models.ChatMessage;
import com.group4.chatapp.models.ChatRoom;
import com.group4.chatapp.models.ChatRoomMember;
import com.group4.chatapp.models.User;
import com.group4.chatapp.repositories.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.jspecify.annotations.Nullable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupChatService {

    private static final String GROUP_EVENT_PREFIX = "[GROUP_EVENT:";

    private final UserService userService;
    private final AttachmentService attachmentService;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final MessageRepository messageRepository;
    private final ChatRoomReadStateRepository chatRoomReadStateRepository;
    private final InvitationRepository invitationRepository;
    private final ChatRoomPinRepository chatRoomPinRepository;
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
            .creatorId(creator.getId())
            .build();

        room = chatRoomRepository.save(room);

        // Create member records - creator is admin
        for (User member : members) {
            var isAdmin = member.getId().equals(creator.getId());
            var chatRoomMember = ChatRoomMember.createMember(room, member, isAdmin);
            chatRoomMemberRepository.save(chatRoomMember);
        }

        publishRoomCreated(room, members);
        publishAddedToGroupNotification(
            room,
            creator,
            members.stream()
                .filter(member -> !member.getId().equals(creator.getId()))
                .collect(Collectors.toSet())
        );

        // Return the created group
        return buildGroupChatDto(room, creator.getId());
    }

    @Transactional
    public GroupChatDto updateGroup(long roomId, GroupChatUpdateDto dto) {
        return updateGroupInternal(roomId, dto.name(), dto.avatarId(), null);
    }

    @Transactional
    public GroupChatDto updateGroup(long roomId, GroupChatUpdateMultipartDto dto) {
        return updateGroupInternal(roomId, dto.name(), null, dto.avatar());
    }

    private GroupChatDto updateGroupInternal(
        long roomId,
        @Nullable String nextNameRaw,
        @Nullable Long avatarId,
        @Nullable MultipartFile avatarFile
    ) {
        var user = userService.getUserOrThrows();
        var room = getGroupRoomOrThrow(roomId);

        // Check if user is admin
        if (!chatRoomMemberRepository.isUserAdmin(roomId, user.getId())) {
            throw new ApiException(
                HttpStatus.FORBIDDEN,
                "Only admin can update group"
            );
        }

        var changed = false;

        // Update name if provided
        var normalizedName = normalizeGroupName(nextNameRaw);
        if (normalizedName != null && !normalizedName.equals(room.getName())) {
            room.setName(normalizedName);
            changed = true;
        }

        if (avatarFile != null && !avatarFile.isEmpty()) {
            var avatar = attachmentService.uploadAvatar(avatarFile);
            room.setAvatar(avatar);
            changed = true;
        } else if (avatarId != null) {
            var avatar = attachmentService.getAttachmentOrThrow(avatarId);
            validateGroupAvatar(avatar);
            room.setAvatar(avatar);
            changed = true;
        }

        if (!changed) {
            return buildGroupChatDto(room, user.getId());
        }

        room = chatRoomRepository.save(room);
        publishGroupUpdated(room);

        return buildGroupChatDto(room, user.getId());
    }

    private void validateGroupAvatar(Attachment avatar) {
        if (avatar.isImage()) {
            return;
        }

        throw new ApiException(
            HttpStatus.BAD_REQUEST,
            "Avatar must be an image"
        );
    }

    @Nullable
    private String normalizeGroupName(@Nullable String rawName) {
        if (rawName == null) {
            return null;
        }

        var trimmed = rawName.trim();
        if (trimmed.isEmpty()) {
            return null;
        }

        if (trimmed.length() > 100) {
            throw new ApiException(
                HttpStatus.BAD_REQUEST,
                "Group name must be at most 100 characters"
            );
        }

        return trimmed;
    }

    @Transactional
    public GroupChatDto addMembers(long roomId, List<Long> memberIds) {
        var user = userService.getUserOrThrows();
        var room = getGroupRoomOrThrow(roomId);

        // Any current member can add new members
        if (!chatRoomMemberRepository.isUserMember(roomId, user.getId())) {
            throw new ApiException(
                HttpStatus.FORBIDDEN,
                "Only group members can add members"
            );
        }

        var newMembers = new HashSet<User>();

        for (Long memberId : memberIds) {
            var targetUser = userService.getUserById(memberId)
                .orElseThrow(() -> new ApiException(
                    HttpStatus.NOT_FOUND,
                    "User not found: " + memberId
                ));

            // Check if already a member (query is safer than entity equals/hashCode)
            if (chatRoomMemberRepository.isUserMember(roomId, targetUser.getId())) {
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
        publishAddedToGroupNotification(room, user, newMembers);

        // Notify members about new members
        var newMemberUsernames = newMembers.stream()
            .map(User::getUsername)
            .collect(Collectors.toList());
        if (!newMemberUsernames.isEmpty()) {
            publishGroupMembershipMessage(
                room,
                user,
                "ADDED",
                "%s added %s.".formatted(user.getUsername(), String.join(", ", newMemberUsernames))
            );
        }

        notifyMembers(roomId, "members_added", Map.of(
            "roomId", roomId,
            "newMembers", newMemberUsernames,
            "addedBy", user.getUsername()
        ));

        return buildGroupChatDto(room, user.getId());
    }

    @Transactional
    public void removeMember(long roomId, long targetUserId) {
        var user = userService.getUserOrThrows();
        var room = getGroupRoomOrThrow(roomId);

        if (!chatRoomMemberRepository.isUserMember(roomId, user.getId())) {
            throw new ApiException(
                HttpStatus.FORBIDDEN,
                "You are not a member of this group"
            );
        }

        if (!chatRoomMemberRepository.isUserMember(roomId, targetUserId)) {
            throw new ApiException(
                HttpStatus.NOT_FOUND,
                "User is not a member of this group"
            );
        }

        Long creatorId = resolveCreatorId(room);
        boolean isOwner = creatorId != null && creatorId.equals(user.getId());

        if (!isOwner) {
            throw new ApiException(
                HttpStatus.FORBIDDEN,
                "Only group owner can remove members"
            );
        }

        if (creatorId != null && creatorId.equals(targetUserId)) {
            throw new ApiException(
                HttpStatus.BAD_REQUEST,
                "Group creator cannot be removed"
            );
        }

        var removedUser = removeMemberFromRoom(room, targetUserId);
        chatRoomPinRepository.deleteByUser_IdAndChatRoom_Id(targetUserId, roomId);
        publishRoomRemovedToUser(removedUser.getUsername(), roomId, "removed");
        publishGroupMembershipMessage(
            room,
            user,
            "REMOVED",
            "%s removed %s from the group.".formatted(
                user.getUsername(),
                removedUser.getUsername()
            )
        );

        // Notify remaining members
        notifyMembers(roomId, "member_removed", Map.of(
            "roomId", roomId,
            "removedUserId", targetUserId,
            "removedUsername", removedUser.getUsername(),
            "action", "removed",
            "actionBy", user.getUsername()
        ));
    }

    @Transactional
    public void leaveGroup(long roomId) {
        var user = userService.getUserOrThrows();
        var room = getGroupRoomOrThrow(roomId);

        if (!chatRoomMemberRepository.isUserMember(roomId, user.getId())) {
            throw new ApiException(
                HttpStatus.FORBIDDEN,
                "You are not a member of this group"
            );
        }

        Long creatorId = resolveCreatorId(room);
        if (creatorId != null && creatorId.equals(user.getId())) {
            throw new ApiException(
                HttpStatus.BAD_REQUEST,
                "Group creator cannot leave the group"
            );
        }

        var removedUser = removeMemberFromRoom(room, user.getId());
        chatRoomPinRepository.deleteByUser_IdAndChatRoom_Id(user.getId(), roomId);
        publishRoomRemovedToUser(removedUser.getUsername(), roomId, "left");
        publishGroupMembershipMessage(
            room,
            removedUser,
            "LEFT",
            "%s left the group.".formatted(removedUser.getUsername())
        );

        notifyMembers(roomId, "member_removed", Map.of(
            "roomId", roomId,
            "removedUserId", user.getId(),
            "removedUsername", removedUser.getUsername(),
            "action", "left",
            "actionBy", removedUser.getUsername()
        ));
    }

    @Transactional
    public void dissolveGroup(long roomId) {
        var user = userService.getUserOrThrows();
        var room = getGroupRoomOrThrow(roomId);

        Long creatorId = resolveCreatorId(room);
        boolean isOwner = creatorId != null && creatorId.equals(user.getId());
        if (!isOwner) {
            throw new ApiException(
                HttpStatus.FORBIDDEN,
                "Only group owner can dissolve this group"
            );
        }

        var memberUsernames = room.getMembers().stream()
            .map(User::getUsername)
            .filter(username -> username != null && !username.isBlank())
            .collect(Collectors.toSet());

        chatRoomReadStateRepository.deleteByRoomId(roomId);
        invitationRepository.deleteByChatRoomId(roomId);
        chatRoomPinRepository.deleteByChatRoom_Id(roomId);
        messageRepository.deleteByRoomId(roomId);
        chatRoomMemberRepository.deleteByChatRoomId(roomId);
        chatRoomRepository.delete(room);

        var payload = Map.of(
            "roomId", roomId,
            "dissolvedBy", user.getUsername()
        );

        memberUsernames.forEach(username ->
            messagingTemplate.convertAndSendToUser(
                username,
                "/queue/friends/removed/",
                payload
            )
        );
    }

    @Transactional
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
        var latestMessage = messageRepository.findFirstByRoom_IdOrderBySentOnDescIdDesc(room.getId())
            .orElse(null);

        var members = room.getMembers().stream()
            .map(UserWithAvatarDto::new)
            .collect(Collectors.toList());

        boolean isAdmin = chatRoomMemberRepository.isUserAdmin(room.getId(), currentUserId);
        Long creatorId = resolveCreatorId(room);
        boolean isOwner = creatorId != null && creatorId.equals(currentUserId);

        return new GroupChatDto(room, latestMessage, members, isAdmin, isOwner);
    }

    private Long resolveCreatorId(ChatRoom room) {
        if (room.getCreatorId() != null) {
            return room.getCreatorId();
        }

        var members = chatRoomMemberRepository.findByRoomId(room.getId());
        var joinedAtComparator = Comparator.comparing(
            ChatRoomMember::getJoinedAt,
            Comparator.nullsLast(Comparator.naturalOrder())
        );

        var adminCreatorId = members.stream()
            .filter(ChatRoomMember::isAdmin)
            .sorted(joinedAtComparator)
            .map(member -> member.getUser().getId())
            .findFirst()
            .orElse(null);

        if (adminCreatorId != null) {
            return adminCreatorId;
        }

        return members.stream()
            .sorted(joinedAtComparator)
            .map(member -> member.getUser().getId())
            .findFirst()
            .orElse(null);
    }

    private User removeMemberFromRoom(ChatRoom room, long targetUserId) {
        var targetMembers = chatRoomMemberRepository.findByRoomIdAndUserId(room.getId(), targetUserId);
        if (targetMembers.isEmpty()) {
            throw new ApiException(
                HttpStatus.NOT_FOUND,
                "User is not a member of this group"
            );
        }

        var targetUser = targetMembers.get(0).getUser();

        room.getMembers().remove(targetUser);
        chatRoomMemberRepository.deleteByChatRoomIdAndUserId(room.getId(), targetUserId);
        chatRoomReadStateRepository.deleteByRoomIdAndReaderId(room.getId(), targetUserId);

        chatRoomRepository.save(room);

        return targetUser;
    }

    private void publishGroupMembershipMessage(
        ChatRoom room,
        User actor,
        String eventType,
        String humanMessage
    ) {
        var message = ChatMessage.builder()
            .sender(actor)
            .room(room)
            .replyTo(null)
            .message(GROUP_EVENT_PREFIX + eventType + "] " + humanMessage)
            .lastEdit(null)
            .attachments(List.of())
            .status(ChatMessage.Status.NORMAL)
            .build();

        var saved = messageRepository.save(message);
        var payload = new MessageReceiveDto(saved);

        room.getMembers().forEach(member ->
            messagingTemplate.convertAndSendToUser(
                member.getUsername(),
                room.getSocketPath(),
                payload
            )
        );
    }

    private void notifyMembers(long roomId, String event, Map<String, Object> payload) {
        var room = chatRoomRepository.findById(roomId).orElse(null);
        if (room == null) return;

        room.getMembers().forEach(member -> {
            var username = member.getUsername();
            if (username == null || username.isBlank()) {
                return;
            }

            messagingTemplate.convertAndSendToUser(
                username,
                "/queue/groups/" + event,
                payload
            );
        });
    }

    private void publishRoomCreated(ChatRoom room, Set<User> targets) {
        if (targets.isEmpty()) {
            return;
        }

        var chatRoomDto = new ChatRoomDto(room, null);
        var payload = Map.of("chatRoom", chatRoomDto);

        targets.forEach(member -> {
            var username = member.getUsername();
            if (username == null || username.isBlank()) {
                return;
            }

            messagingTemplate.convertAndSendToUser(
                username,
                "/queue/chatrooms/created/",
                payload
            );
        });
    }

    private void publishGroupUpdated(ChatRoom room) {
        var latestMessage = messageRepository
            .findFirstByRoom_IdOrderBySentOnDescIdDesc(room.getId())
            .orElse(null);

        room.getMembers().forEach(member -> {
            var username = member.getUsername();
            if (username == null || username.isBlank()) {
                return;
            }

            var chatRoomDto = new ChatRoomDto(room, latestMessage);
            var isPinned = chatRoomPinRepository.existsByUser_IdAndChatRoom_Id(
                member.getId(),
                room.getId()
            );
            chatRoomDto.setPinned(isPinned);

            var payload = Map.of(
                "roomId", room.getId(),
                "chatRoom", chatRoomDto
            );

            messagingTemplate.convertAndSendToUser(
                username,
                "/queue/groups/group_updated",
                payload
            );
        });
    }

    private void publishRoomRemovedToUser(@Nullable String username, long roomId, String action) {
        if (username == null || username.isBlank()) {
            return;
        }

        messagingTemplate.convertAndSendToUser(
            username,
            "/queue/friends/removed/",
            Map.of(
                "roomId", roomId,
                "action", action
            )
        );
    }

    private void publishAddedToGroupNotification(
        ChatRoom room,
        User actor,
        Set<User> targets
    ) {
        if (targets.isEmpty()) {
            return;
        }

        var roomName = room.getName();
        if (roomName == null || roomName.isBlank()) {
            roomName = "Group chat";
        }

        var addedBy = actor.getUsername();
        if (addedBy == null || addedBy.isBlank()) {
            addedBy = "Someone";
        }

        var payload = Map.<String, Object>of(
            "roomId", room.getId(),
            "roomName", roomName,
            "addedBy", addedBy
        );

        targets.forEach(member -> {
            var username = member.getUsername();
            if (username == null || username.isBlank()) {
                return;
            }

            messagingTemplate.convertAndSendToUser(
                username,
                "/queue/groups/added/",
                payload
            );
        });
    }
}
