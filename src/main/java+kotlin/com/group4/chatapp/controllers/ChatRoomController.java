package com.group4.chatapp.controllers;

import com.group4.chatapp.dtos.ChatRoomDto;
import com.group4.chatapp.services.ChatRoomService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@SecurityRequirements({
    @SecurityRequirement(name = "basicAuth"),
    @SecurityRequirement(name = "bearerAuth")
})
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @GetMapping("/api/v1/chatrooms/")
    public List<ChatRoomDto> listChatRooms() {
        return chatRoomService.listRoomsWithLatestMessage();
    }
@MessageMapping
    @DeleteMapping("/api/v1/chatrooms/{roomId}/friend/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFriend(@PathVariable long roomId) {
        chatRoomService.removeFriend(roomId);
    }

    @PostMapping("/api/v1/chatrooms/{roomId}/pin/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void pinRoom(@PathVariable long roomId) {
        chatRoomService.pinRoom(roomId);
    }

    @DeleteMapping("/api/v1/chatrooms/{roomId}/pin/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unpinRoom(@PathVariable long roomId) {
        chatRoomService.unpinRoom(roomId);
    }
}
