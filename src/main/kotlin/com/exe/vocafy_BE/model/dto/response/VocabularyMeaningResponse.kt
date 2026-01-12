package com.exe.vocafy_BE.model.dto.response

import com.exe.vocafy_BE.enum.LanguageCode
import com.exe.vocafy_BE.enum.PartOfSpeech
import com.fasterxml.jackson.annotation.JsonProperty

data class VocabularyMeaningResponse(
    val id: Long,
    @JsonProperty("language_code")
    val languageCode: LanguageCode,
    @JsonProperty("meaning_text")
    val meaningText: String,
    @JsonProperty("example_sentence")
    val exampleSentence: String?,
    @JsonProperty("example_translation")
    val exampleTranslation: String?,
    @JsonProperty("part_of_speech")
    val partOfSpeech: PartOfSpeech,
    @JsonProperty("sense_order")
    val senseOrder: Int?,
)
