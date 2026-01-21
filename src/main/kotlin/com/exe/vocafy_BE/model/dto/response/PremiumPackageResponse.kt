package com.exe.vocafy_BE.model.dto.response

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class PremiumPackageResponse(
    val id: Long,
    val name: String,
    val description: String?,
    val price: Int,
    @JsonProperty("duration_days")
    val durationDays: Int,
    val active: Boolean,
    @JsonProperty("created_at")
    val createdAt: LocalDateTime?,
)
