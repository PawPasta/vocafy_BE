package com.exe.vocafy_BE.model.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotNull

data class EnrollmentCreateRequest(
    @JsonProperty("syllabus_id")
    @field:NotNull(message = "'syllabus_id' can't be null")
    val syllabusId: Long? = null,
)
