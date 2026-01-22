package com.exe.vocafy_BE.model.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class TopicCreateRequest(
    @field:NotBlank(message = "'title' can't be null")
    val title: String? = null,

    val description: String? = null,

    @JsonProperty("total_days")
    @field:NotNull(message = "'total_days' can't be null")
    @field:Min(value = 1, message = "'total_days' must be at least 1")
    val totalDays: Int? = null,

    @JsonProperty("sort_order")
    @field:NotNull(message = "'sort_order' can't be null")
    val sortOrder: Int? = null,

    @JsonProperty("course_ids")
    val courseIds: List<Long>? = null,
)

