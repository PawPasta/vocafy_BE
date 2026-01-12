package com.exe.vocafy_BE.model.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class VocabularyResponse(
    val id: Long,
    @JsonProperty("course_id")
    val courseId: Long,
    val note: String?,
    @JsonProperty("sort_order")
    val sortOrder: Int,
    val terms: List<VocabularyTermResponse> = emptyList(),
    val meanings: List<VocabularyMeaningResponse> = emptyList(),
    val medias: List<VocabularyMediaResponse> = emptyList(),
    @JsonProperty("created_at")
    val createdAt: LocalDateTime?,
    @JsonProperty("updated_at")
    val updatedAt: LocalDateTime?,
)
