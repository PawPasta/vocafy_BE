package com.exe.vocafy_BE.model.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank

data class ProfileUpdateRequest(
    @JsonProperty("display_name")
    @field:NotBlank(message = "'display_name' can't be null")
    val displayName: String? = null,

    @JsonProperty("avatar_url")
    val avatarUrl: String? = null,

    val locale: String? = null,

    val timezone: String? = null,
)
