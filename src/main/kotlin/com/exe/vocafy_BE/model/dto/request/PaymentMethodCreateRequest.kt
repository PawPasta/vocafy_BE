package com.exe.vocafy_BE.model.dto.request

import jakarta.validation.constraints.NotBlank

data class PaymentMethodCreateRequest(
    @field:NotBlank(message = "'provider' can't be null")
    val provider: String? = null,
    val description: String? = null,
)
