package com.group4.chatapp.dtos.group;

import org.jspecify.annotations.Nullable;
import org.springframework.web.multipart.MultipartFile;

public record GroupChatUpdateMultipartDto(
    @Nullable String name,
    @Nullable MultipartFile avatar
) {
}
