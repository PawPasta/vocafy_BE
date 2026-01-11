package com.exe.vocafy_BE.model.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class CourseResponse(
    val id: Long,
    val title: String,
    val description: String?,
    @JsonProperty("created_by_user_id")
    val createdByUserId: String?,
    @JsonProperty("created_at")
    val createdAt: LocalDateTime?,
    @JsonProperty("updated_at")
    val updatedAt: LocalDateTime?,
)
