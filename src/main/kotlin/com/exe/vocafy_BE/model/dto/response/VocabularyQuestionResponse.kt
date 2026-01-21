package com.exe.vocafy_BE.model.dto.response

import com.exe.vocafy_BE.enum.VocabularyQuestionType
import com.fasterxml.jackson.annotation.JsonProperty

data class VocabularyQuestionResponse(
    @JsonProperty("question_type")
    val questionType: VocabularyQuestionType,
    @JsonProperty("question_text")
    val questionText: String,
    @JsonProperty("question_ref")
    val questionRef: VocabularyQuestionRefResponse,
    val options: List<VocabularyQuestionRefResponse>,
    @JsonProperty("difficulty_level")
    val difficultyLevel: Short,
)
