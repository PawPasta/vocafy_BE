package com.exe.vocafy_BE.model.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

data class PremiumPackageCreateRequest(
    @field:NotBlank
    @field:Size(max = 100)
    val name: String,

    val description: String? = null,

    @field:NotNull
    @field:Min(0)
    val price: Int,

    @JsonProperty("duration_days")
    @field:NotNull
    @field:Min(1)
    val durationDays: Int,

    val active: Boolean = true,
)

