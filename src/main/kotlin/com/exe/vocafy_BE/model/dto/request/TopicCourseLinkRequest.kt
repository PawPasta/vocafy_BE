package com.exe.vocafy_BE.model.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotEmpty

data class TopicCourseLinkRequest(
    @JsonProperty("course_ids")
    @field:NotEmpty(message = "'course_ids' can't be empty")
    val courseIds: List<Long>,
)
