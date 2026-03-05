package com.exe.vocafy_BE.model.dto.response

import com.fasterxml.jackson.annotation.JsonProperty

data class FeedbackRatingSummaryResponse(
    @JsonProperty("total_ratings")
    val totalRatings: Long,
    @JsonProperty("rating_5")
    val rating5: Long,
    @JsonProperty("rating_4")
    val rating4: Long,
    @JsonProperty("rating_3")
    val rating3: Long,
    @JsonProperty("rating_2")
    val rating2: Long,
    @JsonProperty("rating_1")
    val rating1: Long,
)
