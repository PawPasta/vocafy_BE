package com.exe.vocafy_BE.model.dto.response

import com.exe.vocafy_BE.enum.EnrollmentStatus
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

data class EnrolledSyllabusResponse(
    @JsonProperty("enrollment_id")
    val enrollmentId: Long,
    val status: EnrollmentStatus,
    @JsonProperty("start_date")
    val startDate: LocalDate,
    @JsonProperty("is_focused")
    val isFocused: Boolean,
    val syllabus: SyllabusResponse,
)
