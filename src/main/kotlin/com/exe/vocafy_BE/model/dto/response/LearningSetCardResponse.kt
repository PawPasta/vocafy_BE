package com.exe.vocafy_BE.model.dto.response

import com.exe.vocafy_BE.enum.LearningSetCardType
import com.fasterxml.jackson.annotation.JsonProperty

data class LearningSetCardResponse(
    @JsonProperty("order_index")
    val orderIndex: Int,
    @JsonProperty("vocab_id")
    val vocabId: Long,
    @JsonProperty("card_type")
    val cardType: LearningSetCardType,
    val vocab: LearningSetVocabularyResponse,
)
