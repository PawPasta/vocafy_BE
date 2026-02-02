package com.exe.vocafy_BE.model.dto.response

import com.fasterxml.jackson.annotation.JsonProperty

data class LearningStateUpdateResponse(
    @JsonProperty("vocab_id")
    val vocabId: Long,
    @JsonProperty("is_correct")
    val isCorrect: Boolean,
    @JsonProperty("prev_state")
    val prevState: String,
    @JsonProperty("new_state")
    val newState: String,
    @JsonProperty("correct_streak")
    val correctStreak: Short,
    @JsonProperty("wrong_streak")
    val wrongStreak: Short,
)
