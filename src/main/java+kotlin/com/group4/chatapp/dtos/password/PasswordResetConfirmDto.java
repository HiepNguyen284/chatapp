package com.group4.chatapp.dtos.password;

import jakarta.validation.constraints.NotBlank;

public record PasswordResetConfirmDto(
    @NotBlank
    String token,
    @NotBlank
    String newPassword
) {}
