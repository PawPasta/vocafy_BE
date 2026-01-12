package com.exe.vocafy_BE.model.dto.request

import com.exe.vocafy_BE.enum.LanguageCode
import com.exe.vocafy_BE.enum.ScriptType
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class VocabularyTermRequest(
    @JsonProperty("language_code")
    @field:NotNull(message = "'language_code' can't be null")
    val languageCode: LanguageCode? = null,

    @JsonProperty("script_type")
    @field:NotNull(message = "'script_type' can't be null")
    val scriptType: ScriptType? = null,

    @JsonProperty("text_value")
    @field:NotBlank(message = "'text_value' can't be null")
    val textValue: String? = null,

    @JsonProperty("extra_meta")
    val extraMeta: String? = null,
)
