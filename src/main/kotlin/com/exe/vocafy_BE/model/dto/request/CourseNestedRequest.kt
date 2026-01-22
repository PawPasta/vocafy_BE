package com.exe.vocafy_BE.model.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class CourseNestedRequest(
    @field:NotBlank(message = "'title' can't be null")
    val title: String? = null,

    val description: String? = null,

    @JsonProperty("sort_order")
    @field:NotNull(message = "'sort_order' can't be null")
    val sortOrder: Int? = null,

    @Valid
    val vocabularies: List<VocabularyNestedRequest>? = null,
)

