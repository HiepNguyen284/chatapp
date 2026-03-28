package com.group4.chatapp.controllers;

import com.group4.chatapp.dtos.group.GroupChatCreateDto;
import com.group4.chatapp.dtos.group.GroupChatDto;
import com.group4.chatapp.dtos.group.GroupChatUpdateDto;
import com.group4.chatapp.dtos.group.GroupMembersAddDto;
import com.group4.chatapp.services.GroupChatService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SecurityRequirements({
    @SecurityRequirement(name = "basicAuth"),
    @SecurityRequirement(name = "bearerAuth")
})
@RequiredArgsConstructor
public class GroupChatController {

    private final GroupChatService groupChatService;

    @PostMapping("/api/v1/chatrooms/groups/")
    @ResponseStatus(HttpStatus.CREATED)
    public GroupChatDto createGroup(@Valid @RequestBody GroupChatCreateDto dto) {
        return groupChatService.createGroup(dto);
    }

    @GetMapping("/api/v1/chatrooms/{roomId}/groups/")
    public GroupChatDto getGroupDetails(@PathVariable long roomId) {
        return groupChatService.getGroupDetails(roomId);
    }

    @PatchMapping("/api/v1/chatrooms/{roomId}/groups/")
    public GroupChatDto updateGroup(
        @PathVariable long roomId,
        @Valid @RequestBody GroupChatUpdateDto dto
    ) {
        return groupChatService.updateGroup(roomId, dto);
    }

    @PostMapping("/api/v1/chatrooms/{roomId}/groups/members/")
    public GroupChatDto addMembers(
        @PathVariable long roomId,
        @Valid @RequestBody GroupMembersAddDto dto
    ) {
        return groupChatService.addMembers(roomId, dto.memberIds());
    }

    @DeleteMapping("/api/v1/chatrooms/{roomId}/groups/members/{userId}/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeMember(
        @PathVariable long roomId,
        @PathVariable long userId
    ) {
        groupChatService.removeMember(roomId, userId);
    }

    @DeleteMapping("/api/v1/chatrooms/{roomId}/groups/leave/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void leaveGroup(@PathVariable long roomId) {
        groupChatService.leaveGroup(roomId);
    }

    @DeleteMapping("/api/v1/chatrooms/{roomId}/groups/dissolve/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void dissolveGroup(@PathVariable long roomId) {
        groupChatService.dissolveGroup(roomId);
    }
}
