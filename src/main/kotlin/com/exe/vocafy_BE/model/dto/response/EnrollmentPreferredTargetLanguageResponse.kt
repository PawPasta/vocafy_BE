package com.exe.vocafy_BE.model.dto.response

import com.exe.vocafy_BE.enum.LanguageCode
import com.fasterxml.jackson.annotation.JsonProperty

data class EnrollmentPreferredTargetLanguageResponse(
    @JsonProperty("syllabus_id")
    val syllabusId: Long,
    @JsonProperty("preferred_target_language")
    val preferredTargetLanguage: LanguageCode?,
)
