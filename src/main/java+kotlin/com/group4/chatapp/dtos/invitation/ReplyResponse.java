package com.group4.chatapp.dtos.invitation;

import com.group4.chatapp.dtos.ChatRoomDto;
import org.jspecify.annotations.Nullable;

public record ReplyResponse(@Nullable ChatRoomDto newChatRoom) {}
