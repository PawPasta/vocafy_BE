package com.exe.vocafy_BE.model.dto.response

import com.exe.vocafy_BE.enum.Role
import com.exe.vocafy_BE.enum.Status
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime
import java.util.UUID

data class UserResponse(
    val id: UUID,
    val email: String,
    val role: Role,
    val status: Status,
    @JsonProperty("last_login_at")
    val lastLoginAt: LocalDateTime?,
    @JsonProperty("last_active_at")
    val lastActiveAt: LocalDateTime?,
    @JsonProperty("sepay_code")
    val sepayCode: String?,
    @JsonProperty("fcm_token")
    val fcmToken: String?,
    val profile: ProfileResponse?,
    @JsonProperty("created_at")
    val createdAt: LocalDateTime?,
    @JsonProperty("updated_at")
    val updatedAt: LocalDateTime?,
)

