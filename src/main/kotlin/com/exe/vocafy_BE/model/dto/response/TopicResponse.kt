package com.exe.vocafy_BE.model.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class TopicResponse(
    val id: Long,
    @JsonProperty("syllabus_id")
    val syllabusId: Long?,
    @JsonProperty("created_by_user_id")
    val createdByUserId: String?,
    val title: String,
    val description: String?,
    @JsonProperty("total_days")
    val totalDays: Int,
    @JsonProperty("sort_order")
    val sortOrder: Int,
    @JsonProperty("is_active")
    val isActive: Boolean,
    @JsonProperty("is_deleted")
    val isDeleted: Boolean,
    val courses: List<CourseResponse>? = null,
    @JsonProperty("created_at")
    val createdAt: LocalDateTime?,
    @JsonProperty("updated_at")
    val updatedAt: LocalDateTime?,
)
