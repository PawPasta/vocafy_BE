package com.exe.vocafy_BE.model.dto.response

import com.fasterxml.jackson.annotation.JsonProperty

data class SyllabusTopicResponse(
    val id: Long,
    @JsonProperty("course_id")
    val courseId: Long,
    val title: String,
    val description: String?,
    @JsonProperty("sort_order")
    val sortOrder: Int,
)
