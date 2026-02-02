package com.exe.vocafy_BE.model.dto.request

import com.exe.vocafy_BE.enum.VocabularyQuestionType
import com.fasterxml.jackson.annotation.JsonProperty

data class LearningAnswerRequest(
    @JsonProperty("question_type")
    val questionType: VocabularyQuestionType,
    @JsonProperty("question_ref")
    val questionRef: LearningAnswerRefRequest,
    @JsonProperty("answer_id")
    val answerId: Long,
)

data class LearningAnswerRefRequest(
    val type: String,
    val id: Long,
)
