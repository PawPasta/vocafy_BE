package com.exe.vocafy_BE.model.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size

data class PremiumPackageUpdateRequest(
    @field:Size(max = 100)
    val name: String? = null,

    val description: String? = null,

    @field:Min(0)
    val price: Int? = null,

    @JsonProperty("duration_days")
    @field:Min(1)
    val durationDays: Int? = null,

    val active: Boolean? = null,
)

