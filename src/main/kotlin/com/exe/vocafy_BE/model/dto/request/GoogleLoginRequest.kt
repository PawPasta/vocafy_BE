package com.exe.vocafy_BE.model.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank

data class GoogleLoginRequest(
    @JsonProperty("id_token")
    @field:NotBlank(message = "'id_token' can't be null")
    val idToken: String? = null,
)
