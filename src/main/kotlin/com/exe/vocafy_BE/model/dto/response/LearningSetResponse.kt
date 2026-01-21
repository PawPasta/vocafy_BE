package com.exe.vocafy_BE.model.dto.response

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class LearningSetResponse(
    val available: Boolean,
    val reason: String? = null,
    val cards: List<LearningSetCardResponse> = emptyList(),
)
