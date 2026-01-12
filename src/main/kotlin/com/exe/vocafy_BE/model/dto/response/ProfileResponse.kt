package com.exe.vocafy_BE.model.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class ProfileResponse(
    val id: UUID,
    @JsonProperty("user_id")
    val userId: UUID,
    @JsonProperty("display_name")
    val displayName: String,
    @JsonProperty("avatar_url")
    val avatarUrl: String?,
    val locale: String?,
    val timezone: String?,
)
