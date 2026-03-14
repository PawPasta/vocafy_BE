package com.exe.vocafy_BE.model.dto.response

import com.fasterxml.jackson.annotation.JsonProperty

data class VocabularyLearningStatusResponse(
    @JsonProperty("vocab_id")
    val vocabId: Long,
    @JsonProperty("learning_state")
    val learningState: String,
    @JsonProperty("learning_progress_percent")
    val learningProgressPercent: Int,
)
