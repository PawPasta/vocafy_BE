package com.exe.vocafy_BE.model.dto.response

data class PaymentMethodResponse(
    val id: Long,
    val provider: String,
    val description: String?,
    val active: Boolean,
)
