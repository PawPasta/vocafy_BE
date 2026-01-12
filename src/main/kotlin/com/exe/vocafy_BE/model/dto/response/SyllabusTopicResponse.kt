package com.exe.vocafy_BE.model.dto.response

import com.fasterxml.jackson.annotation.JsonProperty

data class SyllabusTopicResponse(
    val id: Long,
    val title: String,
    val description: String?,
    @JsonProperty("total_days")
    val totalDays: Int,
    @JsonProperty("sort_order")
    val sortOrder: Int,
    val courses: List<SyllabusTopicCourseResponse> = emptyList(),
)
