package com.exe.vocafy_BE.model.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotNull

data class VocabularyUpdateRequest(
    @JsonProperty("course_id")
    @field:NotNull(message = "'course_id' can't be null")
    val courseId: Long? = null,

    val note: String? = null,

    @JsonProperty("sort_order")
    @field:NotNull(message = "'sort_order' can't be null")
    val sortOrder: Int? = null,

    val terms: List<VocabularyTermRequest>? = null,
    val meanings: List<VocabularyMeaningRequest>? = null,
    val medias: List<VocabularyMediaRequest>? = null,
)
