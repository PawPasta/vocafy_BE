package com.exe.vocafy_BE.model.dto.request

import com.fasterxml.jackson.annotation.JsonProperty

data class LearningSetCompleteRequest(
    @JsonProperty("vocab_ids")
    val vocabIds: List<Long>?,
)
