package com.exe.vocafy_BE.model.dto.response

import com.exe.vocafy_BE.enum.MediaType
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class VocabularyMediaResponse(
    val id: Long,
    @JsonProperty("media_type")
    val mediaType: MediaType,
    val url: String,
    val meta: String?,
    @JsonProperty("created_at")
    val createdAt: LocalDateTime?,
)
