package com.exe.vocafy_BE.model.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotEmpty

data class CourseVocabularyLinkRequest(
    @JsonProperty("vocabulary_ids")
    @field:NotEmpty(message = "'vocabulary_ids' can't be empty")
    val vocabularyIds: List<Long>,
)
