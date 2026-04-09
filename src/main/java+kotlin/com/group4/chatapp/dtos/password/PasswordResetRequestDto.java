package com.group4.chatapp.dtos.password;

import jakarta.validation.constraints.NotBlank;

public record PasswordResetRequestDto(
    @NotBlank
    String username
) {}
