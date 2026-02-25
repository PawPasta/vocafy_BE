package com.exe.vocafy_BE.model.dto.request

import com.exe.vocafy_BE.enum.LanguageCode
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotNull

data class EnrollmentPreferredTargetLanguageRequest(
    @JsonProperty("syllabus_id")
    @field:NotNull(message = "'syllabus_id' can't be null")
    val syllabusId: Long? = null,

    @JsonProperty("preferred_target_language")
    @field:NotNull(message = "'preferred_target_language' can't be null")
    val preferredTargetLanguage: LanguageCode? = null,
)
