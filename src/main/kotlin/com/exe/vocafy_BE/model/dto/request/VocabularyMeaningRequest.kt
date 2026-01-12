package com.exe.vocafy_BE.model.dto.request

import com.exe.vocafy_BE.enum.LanguageCode
import com.exe.vocafy_BE.enum.PartOfSpeech
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class VocabularyMeaningRequest(
    @JsonProperty("language_code")
    @field:NotNull(message = "'language_code' can't be null")
    val languageCode: LanguageCode? = null,

    @JsonProperty("meaning_text")
    @field:NotBlank(message = "'meaning_text' can't be null")
    val meaningText: String? = null,

    @JsonProperty("example_sentence")
    val exampleSentence: String? = null,

    @JsonProperty("example_translation")
    val exampleTranslation: String? = null,

    @JsonProperty("part_of_speech")
    @field:NotNull(message = "'part_of_speech' can't be null")
    val partOfSpeech: PartOfSpeech? = null,

    @JsonProperty("sense_order")
    val senseOrder: Int? = null,
)
