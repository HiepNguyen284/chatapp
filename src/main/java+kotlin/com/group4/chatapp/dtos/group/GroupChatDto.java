package com.group4.chatapp.dtos.group;

import com.group4.chatapp.dtos.AttachmentDto;
import com.group4.chatapp.dtos.messages.MessageReceiveDto;
import com.group4.chatapp.dtos.user.UserWithAvatarDto;
import com.group4.chatapp.models.ChatMessage;
import com.group4.chatapp.models.ChatRoom;
import io.swagger.v3.oas.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

import java.sql.Timestamp;
import java.util.List;

@Schema(description = "Group chat details response")
public class GroupChatDto {

    @Schema(description = "Room ID")
    private long id;

    @Schema(description = "Group name")
    private String name;

    @Schema(description = "Group avatar")
    private AttachmentDto avatar;

    @Schema(description = "List of members with avatars")
    private List<UserWithAvatarDto> members;

    @Schema(description = "Room type (DUO or GROUP)")
    private ChatRoom.Type type;

    @Schema(description = "Creation timestamp")
    private Timestamp createdOn;

    @Schema(description = "Latest message")
    private MessageReceiveDto latestMessage;

    @Schema(description = "Is current user an admin")
    private boolean isAdmin;

    public GroupChatDto() {}

    public GroupChatDto(
        ChatRoom room,
        @Nullable ChatMessage latestMessage,
        List<UserWithAvatarDto> members,
        boolean isAdmin
    ) {
        this.id = room.getId();
        this.name = room.getName();
        this.type = room.getType();
        this.createdOn = room.getCreatedOn();
        this.members = members;
        this.isAdmin = isAdmin;

        var avatar = room.getAvatar();
        if (avatar != null) {
            this.avatar = new AttachmentDto(avatar);
        }

        if (latestMessage != null) {
            this.latestMessage = new MessageReceiveDto(latestMessage);
        }
    }

    // Getters
    public long getId() { return id; }
    public String getName() { return name; }
    public AttachmentDto getAvatar() { return avatar; }
    public List<UserWithAvatarDto> getMembers() { return members; }
    public ChatRoom.Type getType() { return type; }
    public Timestamp getCreatedOn() { return createdOn; }
    public MessageReceiveDto getLatestMessage() { return latestMessage; }
    public boolean isAdmin() { return isAdmin; }
}
