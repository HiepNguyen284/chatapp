package com.group4.chatapp.dtos.user

import jakarta.validation.constraints.NotBlank

data class FcmTokenDto(
    @field:NotBlank val token: String
)
