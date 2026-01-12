package com.exe.vocafy_BE.model.dto.request

import com.exe.vocafy_BE.enum.MediaType
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class VocabularyMediaRequest(
    @JsonProperty("media_type")
    @field:NotNull(message = "'media_type' can't be null")
    val mediaType: MediaType? = null,

    @field:NotBlank(message = "'url' can't be null")
    val url: String? = null,

    val meta: String? = null,
)
