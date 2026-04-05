package com.group4.chatapp.dtos.token

import jakarta.validation.constraints.NotBlank

data class TokenRevokeRequestDto(
    @field:NotBlank
    val refresh: String,
)
