package com.group4.chatapp.dtos.group;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.jspecify.annotations.Nullable;

import java.util.List;

@Schema(description = "Request to create a new group chat")
public record GroupChatCreateDto(

    @Schema(description = "Group name", example = "My Group Chat")
    @NotBlank
    @Size(min = 1, max = 100)
    String name,

    @Schema(description = "List of user IDs to add as members (besides the creator)")
    @NotNull
    @Size(min = 2, message = "At least 2 other users required for a group")
    List<Long> memberIds,

    @Nullable
    @Schema(description = "Group avatar attachment ID")
    Long avatarId
) {}
