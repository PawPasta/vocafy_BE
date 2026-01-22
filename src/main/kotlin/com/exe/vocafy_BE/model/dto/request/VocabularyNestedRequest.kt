package com.exe.vocafy_BE.model.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull

data class VocabularyNestedRequest(
    val note: String? = null,

    @JsonProperty("sort_order")
    @field:NotNull(message = "'sort_order' can't be null")
    val sortOrder: Int? = null,

    @Valid
    val terms: List<VocabularyTermRequest>? = null,

    @Valid
    val meanings: List<VocabularyMeaningRequest>? = null,

    @Valid
    val medias: List<VocabularyMediaRequest>? = null,
)

