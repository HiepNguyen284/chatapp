package com.group4.chatapp.dtos.password;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordDto(
    @NotBlank
    String oldPassword,
    @NotBlank
    String newPassword
) {}
