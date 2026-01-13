package com.exe.vocafy_BE.model.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotNull

data class EnrollmentFocusRequest(
    @JsonProperty("syllabus_id")
    @field:NotNull(message = "'syllabus_id' can't be null")
    val syllabusId: Long? = null,
)
