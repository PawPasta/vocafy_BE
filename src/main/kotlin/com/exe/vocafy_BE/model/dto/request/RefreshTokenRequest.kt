package com.exe.vocafy_BE.model.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank

data class RefreshTokenRequest(
    @JsonProperty("refresh_token")
    @field:NotBlank(message = "'refresh_token' can't be null")
    val refreshToken: String? = null,
)
