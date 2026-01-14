package com.exe.vocafy_BE.model.dto.response

import com.exe.vocafy_BE.enum.LanguageCode
import com.exe.vocafy_BE.enum.ScriptType
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class VocabularyTermResponse(
    val id: Long,
    @JsonProperty("language_code")
    val languageCode: LanguageCode,
    @JsonProperty("script_type")
    val scriptType: ScriptType,
    @JsonProperty("text_value")
    val textValue: String,
    @JsonProperty("extra_meta")
    val extraMeta: String?,
    @JsonProperty("created_at")
    val createdAt: LocalDateTime?,
    @JsonProperty("updated_at")
    val updatedAt: LocalDateTime?,
)
