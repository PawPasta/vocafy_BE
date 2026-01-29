package com.exe.vocafy_BE.model.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class VocabularyResponse(
    val id: Long,
    @JsonProperty("course_id")
    val courseId: Long?,
    @JsonProperty("created_by_user_id")
    val createdByUserId: String?,
    val note: String?,
    @JsonProperty("sort_order")
    val sortOrder: Int,
    @JsonProperty("is_active")
    val isActive: Boolean,
    @JsonProperty("is_deleted")
    val isDeleted: Boolean,
    val terms: List<VocabularyTermResponse> = emptyList(),
    val meanings: List<VocabularyMeaningResponse> = emptyList(),
    val medias: List<VocabularyMediaResponse> = emptyList(),
    @JsonProperty("created_at")
    val createdAt: LocalDateTime?,
    @JsonProperty("updated_at")
    val updatedAt: LocalDateTime?,
)
