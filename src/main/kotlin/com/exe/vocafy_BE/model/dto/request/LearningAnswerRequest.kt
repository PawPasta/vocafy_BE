package com.exe.vocafy_BE.model.dto.request

import com.fasterxml.jackson.annotation.JsonProperty

data class LearningAnswerRequest(
    @JsonProperty("vocab_id")
    val vocabId: Long,
    @JsonProperty("is_correct")
    val isCorrect: Boolean,
)
