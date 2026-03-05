package com.exe.vocafy_BE.model.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime
import java.util.UUID

data class FeedbackResponse(
    val id: Long,
    @JsonProperty("user_id")
    val userId: UUID,
    @JsonProperty("user_display_name")
    val userDisplayName: String?,
    @JsonProperty("user_email")
    val userEmail: String,
    val rating: Int,
    val title: String?,
    val content: String?,
    @JsonProperty("admin_reply")
    val adminReply: String?,
    @JsonProperty("replied_by_user_id")
    val repliedByUserId: UUID?,
    @JsonProperty("replied_by_email")
    val repliedByEmail: String?,
    @JsonProperty("replied_at")
    val repliedAt: LocalDateTime?,
    @JsonProperty("created_at")
    val createdAt: LocalDateTime?,
    @JsonProperty("updated_at")
    val updatedAt: LocalDateTime?,
)
