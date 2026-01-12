package com.exe.vocafy_BE.model.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class CourseCreateRequest(
    @field:NotBlank(message = "'title' can't be null")
    val title: String? = null,

    val description: String? = null,

    @JsonProperty("sort_order")
    @field:NotNull(message = "'sort_order' can't be null")
    val sortOrder: Int? = null,
)
