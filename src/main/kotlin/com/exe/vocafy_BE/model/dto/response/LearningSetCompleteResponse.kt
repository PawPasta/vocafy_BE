package com.exe.vocafy_BE.model.dto.response

import com.fasterxml.jackson.annotation.JsonProperty

data class LearningSetCompleteResponse(
    @JsonProperty("updated_count")
    val updatedCount: Int,
)
