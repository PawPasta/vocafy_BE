package com.exe.vocafy_BE.model.dto.response

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class VocabularyQuestionRefResponse(
    val type: String,
    val id: Long,
    val text: String? = null,
    val url: String? = null,
)
