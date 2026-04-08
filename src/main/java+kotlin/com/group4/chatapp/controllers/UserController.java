package com.group4.chatapp.controllers;

import com.group4.chatapp.dtos.token.TokenObtainPairDto;
import com.group4.chatapp.dtos.token.TokenRefreshDto;
import com.group4.chatapp.dtos.token.TokenRefreshRequestDto;
import com.group4.chatapp.dtos.token.TokenRevokeRequestDto;
import com.group4.chatapp.dtos.user.UserDto;
import com.group4.chatapp.dtos.user.UserPresenceDto;
import com.group4.chatapp.dtos.user.UserProfileUpdateDto;
import com.group4.chatapp.dtos.user.UserBlockStatusDto;
import com.group4.chatapp.dtos.user.UserWithAvatarDto;
import com.group4.chatapp.dtos.user.FcmTokenDto;
import com.group4.chatapp.services.FcmTokenService;
import com.group4.chatapp.services.JwtsService;
import com.group4.chatapp.services.UserBlockService;
import com.group4.chatapp.services.PresenceService;
import com.group4.chatapp.services.UserService;
import com.group4.chatapp.services.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users/")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserBlockService userBlockService;
    private final JwtsService jwtsService;
    private final PresenceService presenceService;
    private final FcmTokenService fcmTokenService;
    private final NotificationService notificationService;

    @PostMapping("/register/")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerUser(@Valid @RequestBody UserDto dto) {
        userService.createUser(dto);
    }

    @PostMapping("/token/")
    public TokenObtainPairDto obtainToken(@Valid @RequestBody UserDto dto) {
        return jwtsService.tokenObtainPair(dto);
    }

    @PostMapping("/token/refresh/")
    public TokenRefreshDto refreshToken(
        @Valid @RequestBody TokenRefreshRequestDto dto
    ) {
        return jwtsService.refreshToken(dto.refresh());
    }

    @PostMapping("/token/revoke/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void revokeToken(@Valid @RequestBody TokenRevokeRequestDto dto) {
        jwtsService.revokeRefreshToken(dto.getRefresh());
    }

    @GetMapping("/search/")
    public List<UserWithAvatarDto> searchUser(
        @RequestParam(name = "q") String keyword,
        @RequestParam(name = "limit", defaultValue = "10") int limit
    ) {
        return userService.searchUser(keyword, limit);
    }

    @GetMapping("/{username}/presence/")
    public UserPresenceDto getUserPresence(@PathVariable String username) {
        return presenceService.getPresence(username);
    }

    @GetMapping("/me/")
    public UserWithAvatarDto getMyProfile() {
        return userService.getCurrentProfile();
    }

    @PutMapping(value = "/me/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UserWithAvatarDto updateMyProfile(@ModelAttribute UserProfileUpdateDto dto) {
        return userService.updateCurrentProfile(dto);
    }

    @GetMapping("/blocks/")
    public List<UserWithAvatarDto> listBlockedUsers() {
        return userBlockService.listBlockedUsers();
    }

    @PostMapping("/{username}/block/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void blockUser(@PathVariable String username) {
        userBlockService.blockByUsername(username);
    }

    @DeleteMapping("/{username}/block/")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unblockUser(@PathVariable String username) {
        userBlockService.unblockByUsername(username);
    }

    @GetMapping("/{username}/block-status/")
    public UserBlockStatusDto getBlockStatus(@PathVariable String username) {
        return userBlockService.getBlockStatus(username);
    }

    @PostMapping("/fcm-token/")
    public ResponseEntity<Void> registerFcmToken(@Valid @RequestBody FcmTokenDto dto) {
        if (!notificationService.isFirebaseEnabled()) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
        }
        fcmTokenService.registerToken(dto.getToken());
        return ResponseEntity.noContent().build();
    }
}
