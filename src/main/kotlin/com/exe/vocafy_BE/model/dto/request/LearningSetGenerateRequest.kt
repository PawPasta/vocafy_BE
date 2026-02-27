package com.exe.vocafy_BE.model.dto.request

import com.fasterxml.jackson.annotation.JsonProperty

data class LearningSetGenerateRequest(
    @JsonProperty("syllabus_id")
    val syllabusId: Long? = null,
)
