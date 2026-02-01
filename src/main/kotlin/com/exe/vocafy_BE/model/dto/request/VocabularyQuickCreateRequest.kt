package com.exe.vocafy_BE.model.dto.request

import com.exe.vocafy_BE.enum.LanguageCode
import com.exe.vocafy_BE.enum.PartOfSpeech
import com.exe.vocafy_BE.enum.ScriptType
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class VocabularyQuickCreateRequest(
    @field:NotBlank(message = "'term' can't be null")
    val term: String? = null,

    @JsonProperty("language_code")
    @field:NotNull(message = "'language_code' can't be null")
    val languageCode: LanguageCode? = null,

    @JsonProperty("script_type")
    @field:NotNull(message = "'script_type' can't be null")
    val scriptType: ScriptType? = null,

    @JsonProperty("meaning_text")
    val meaningText: String? = null,

    @JsonProperty("part_of_speech")
    val partOfSpeech: PartOfSpeech? = null,

    @JsonProperty("example_sentence")
    val exampleSentence: String? = null,

    @JsonProperty("example_translation")
    val exampleTranslation: String? = null,

    val note: String? = null,

    @JsonProperty("sort_order")
    val sortOrder: Int? = null,
)
