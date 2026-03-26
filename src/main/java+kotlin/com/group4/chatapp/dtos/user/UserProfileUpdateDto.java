package com.group4.chatapp.dtos.user;

import org.jspecify.annotations.Nullable;
import org.springframework.web.multipart.MultipartFile;

public record UserProfileUpdateDto(
    @Nullable String displayName,
    @Nullable MultipartFile avatar
) {
}
