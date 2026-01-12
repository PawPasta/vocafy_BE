package com.exe.vocafy_BE.model.dto.response

import com.exe.vocafy_BE.enum.SubscriptionPlan
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

data class SubscriptionResponse(
    val plan: SubscriptionPlan,
    @JsonProperty("start_at")
    val startAt: LocalDate?,
    @JsonProperty("end_at")
    val endAt: LocalDate?,
    @JsonProperty("updated_at")
    val updatedAt: java.time.LocalDateTime?,
)
