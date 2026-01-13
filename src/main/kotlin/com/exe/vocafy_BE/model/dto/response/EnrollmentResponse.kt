package com.exe.vocafy_BE.model.dto.response

import com.exe.vocafy_BE.enum.EnrollmentStatus
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

data class EnrollmentResponse(
    val id: Long,
    @JsonProperty("user_id")
    val userId: String,
    @JsonProperty("syllabus_id")
    val syllabusId: Long,
    @JsonProperty("start_date")
    val startDate: LocalDate,
    val status: EnrollmentStatus,
)
