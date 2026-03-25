package com.group4.chatapp.services.messages;

import com.group4.chatapp.dtos.messages.MessageReceiveDto;
import com.group4.chatapp.dtos.messages.MessageReadEventDto;
import com.group4.chatapp.dtos.messages.MessageSendDto;
import com.group4.chatapp.dtos.messages.MessageSendResponseDto;
import com.group4.chatapp.dtos.messages.MessageTypingEventDto;
import com.group4.chatapp.dtos.user.UserWithAvatarDto;
import com.group4.chatapp.models.ChatMessage;
import com.group4.chatapp.models.ChatRoom;
import com.group4.chatapp.models.ChatRoomReadState;
import com.group4.chatapp.models.User;
import com.group4.chatapp.repositories.ChatRoomReadStateRepository;
import com.group4.chatapp.repositories.MessageRepository;
import com.group4.chatapp.services.UserBlockService;
import com.group4.chatapp.services.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageChangesService sendService;
    private final MessageCheckService checkService;
    private final ChatRoomReadStateRepository chatRoomReadStateRepository;
    private final MessageRepository messageRepository;
    private final UserService userService;
    private final UserBlockService userBlockService;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public List<MessageReceiveDto> getMessages(long roomId, int page) {

        if (page < 1) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Page mustn't less than 1!"
            );
        }

        checkService.receiveChatRoomAndCheck(roomId);

        var readStates = chatRoomReadStateRepository.findWithReaderByRoomId(roomId);
        Map<Long, ChatRoomReadState> stateByReaderId = readStates.stream()
            .filter(state -> state.getReader() != null && state.getReader().getId() != null)
            .collect(Collectors.toMap(
                state -> state.getReader().getId(),
                state -> state,
                (a, b) -> a
            ));

        var pageRequest = PageRequest.of(
            page - 1, 50,
            Sort.by(Sort.Direction.DESC, "sentOn")
        );

        var messages = messageRepository.findByRoomId(roomId, pageRequest)
            .map(message -> new MessageReceiveDto(message, buildSeenByUsers(message, stateByReaderId)))
            .collect(Collectors.toList());

        Collections.reverse(messages);

        return messages;
    }

    public MessageSendResponseDto sendMessage(long roomId, MessageSendDto dto) {
        return sendService.sendMessage(roomId, dto);
    }

    public void changeMessage(long messageId, MessageSendDto dto) {
         sendService.editMessage(messageId, dto);
    }

    public void deleteMessage(long messageId) {
        sendService.recallMessage(messageId);
    }

    @Transactional
    public void setTypingStatus(long roomId, boolean typing) {

        var user = userService.getUserOrThrows();
        var chatRoom = checkService.receiveChatRoomAndCheck(roomId, user);
        ensureRoomMessagingAllowed(chatRoom, user);
        var payload = new MessageTypingEventDto(
            chatRoom.getId(),
            user.getUsername(),
            typing
        );

        var socketPath = String.format("/queue/chat/%d/typing", roomId);
        chatRoom.getMembers()
            .stream()
            .filter(member -> !member.equals(user))
            .forEach(member ->
                messagingTemplate.convertAndSendToUser(
                    member.getUsername(),
                    socketPath,
                    payload
                )
            );
    }

    @Transactional
    public void setReadStatus(long roomId) {

        var user = userService.getUserOrThrows();
        var chatRoom = checkService.receiveChatRoomAndCheck(roomId, user);
        ensureRoomMessagingAllowed(chatRoom, user);
        var now = new Timestamp(System.currentTimeMillis());

        var state = chatRoomReadStateRepository
            .findByRoomIdAndReaderId(chatRoom.getId(), user.getId())
            .orElseGet(() -> ChatRoomReadState.builder()
                .room(chatRoom)
                .reader(user)
                .lastReadAt(now)
                .build());

        state.setLastReadAt(now);
        chatRoomReadStateRepository.save(state);

        var payload = new MessageReadEventDto(
            chatRoom.getId(),
            new UserWithAvatarDto(user),
            now
        );

        var socketPath = String.format("/queue/chat/%d/read", roomId);
        chatRoom.getMembers()
            .stream()
            .filter(member -> !member.equals(user))
            .forEach(member ->
                messagingTemplate.convertAndSendToUser(
                    member.getUsername(),
                    socketPath,
                    payload
                )
            );
    }

    private List<User> buildSeenByUsers(
        ChatMessage message,
        Map<Long, ChatRoomReadState> stateByReaderId
    ) {

        if (message.getSentOn() == null) {
            return List.of();
        }

        return stateByReaderId.values()
            .stream()
            .map(ChatRoomReadState::getReader)
            .filter(reader -> reader != null && !reader.equals(message.getSender()))
            .filter(reader -> {
                var state = stateByReaderId.get(reader.getId());
                return state != null
                    && state.getLastReadAt() != null
                    && !message.getSentOn().after(state.getLastReadAt());
            })
            .sorted(Comparator.comparing(User::getUsername))
            .toList();
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
            "Cannot interact in this chat because one user has blocked the other"
        );
    }
}
