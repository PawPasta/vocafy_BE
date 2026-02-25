package com.exe.vocafy_BE.model.dto.response

import com.fasterxml.jackson.annotation.JsonProperty

data class LearningSetVocabularyResponse(
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
    val terms: List<LearningSetVocabularyTermResponse> = emptyList(),
    val meanings: List<LearningSetVocabularyMeaningResponse> = emptyList(),
    val medias: List<LearningSetVocabularyMediaResponse> = emptyList(),
)
