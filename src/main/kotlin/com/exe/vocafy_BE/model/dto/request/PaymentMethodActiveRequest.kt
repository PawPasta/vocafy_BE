package com.exe.vocafy_BE.model.dto.request

import jakarta.validation.constraints.NotNull

data class PaymentMethodActiveRequest(
    @field:NotNull(message = "'active' can't be null")
    val active: Boolean? = null,
)
