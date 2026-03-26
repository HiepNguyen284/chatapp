package com.group4.chatapp.services;

import com.group4.chatapp.dtos.user.UserWithAvatarDto;
import com.group4.chatapp.dtos.user.UserBlockStatusDto;
import com.group4.chatapp.exceptions.ApiException;
import com.group4.chatapp.models.Invitation;
import com.group4.chatapp.models.User;
import com.group4.chatapp.models.UserBlock;
import com.group4.chatapp.repositories.InvitationRepository;
import com.group4.chatapp.repositories.UserBlockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserBlockService {

    private final UserService userService;
    private final UserBlockRepository userBlockRepository;
    private final InvitationRepository invitationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional(readOnly = true)
    public List<UserWithAvatarDto> listBlockedUsers() {
        var me = userService.getUserOrThrows();

        return userBlockRepository.findByBlocker_Id(me.getId())
            .stream()
            .map(UserBlock::getBlocked)
            .map(UserWithAvatarDto::new)
            .toList();
    }

    @Transactional
    public void blockByUsername(String blockedUsername) {
        var me = userService.getUserOrThrows();
        var target = userService.getUserByUsername(blockedUsername)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

        if (me.getId().equals(target.getId())) {
            throw new ApiException(HttpStatus.CONFLICT, "You cannot block yourself");
        }

        var alreadyBlocked = userBlockRepository.existsByBlocker_IdAndBlocked_Id(me.getId(), target.getId());
        if (!alreadyBlocked) {
            userBlockRepository.save(UserBlock.builder()
                .blocker(me)
                .blocked(target)
                .build());
        }

        invitationRepository.deletePendingBetweenUsers(me.getId(), target.getId(), Invitation.Status.PENDING);
        publishStatusToBothUsers(me, target);
    }

    @Transactional
    public void unblockByUsername(String blockedUsername) {
        var me = userService.getUserOrThrows();
        var target = userService.getUserByUsername(blockedUsername)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

        userBlockRepository.deleteByBlocker_IdAndBlocked_Id(me.getId(), target.getId());
        publishStatusToBothUsers(me, target);
    }

    @Transactional(readOnly = true)
    public UserBlockStatusDto getBlockStatus(String username) {
        var me = userService.getUserOrThrows();
        var target = userService.getUserByUsername(username)
            .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

        if (me.getId().equals(target.getId())) {
            throw new ApiException(HttpStatus.CONFLICT, "Cannot query block status with yourself");
        }

        return buildStatus(me, target);
    }

    @Transactional(readOnly = true)
    public boolean isBlockedEitherWay(User first, User second) {
        return userBlockRepository.existsByBlocker_IdAndBlocked_Id(first.getId(), second.getId())
            || userBlockRepository.existsByBlocker_IdAndBlocked_Id(second.getId(), first.getId());
    }

    @Transactional(readOnly = true)
    public void ensureNotBlockedEitherWay(User first, User second, String message) {
        if (isBlockedEitherWay(first, second)) {
            throw new ApiException(HttpStatus.FORBIDDEN, message);
        }
    }

    private void publishStatusToBothUsers(User first, User second) {
        var firstPayload = buildStatus(first, second);
        var secondPayload = buildStatus(second, first);

        messagingTemplate.convertAndSendToUser(
            first.getUsername(),
            "/queue/users/block/",
            firstPayload
        );

        messagingTemplate.convertAndSendToUser(
            second.getUsername(),
            "/queue/users/block/",
            secondPayload
        );
    }

    private UserBlockStatusDto buildStatus(User me, User other) {
        var blockedByMe = userBlockRepository.existsByBlocker_IdAndBlocked_Id(me.getId(), other.getId());
        var blockedByUser = userBlockRepository.existsByBlocker_IdAndBlocked_Id(other.getId(), me.getId());

        return new UserBlockStatusDto(other.getUsername(), blockedByMe, blockedByUser);
    }
}
