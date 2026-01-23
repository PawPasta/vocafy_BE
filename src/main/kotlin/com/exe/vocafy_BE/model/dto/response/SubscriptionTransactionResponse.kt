package com.exe.vocafy_BE.model.dto.response

import com.exe.vocafy_BE.enum.SubscriptionTransactionStatus
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime
import java.util.UUID

data class SubscriptionTransactionResponse(
    val id: UUID,
    val user: SubscriptionTransactionUserResponse,
    @JsonProperty("payment_method_id")
    val paymentMethodId: Long,
    val amount: Long,
    val status: SubscriptionTransactionStatus,
    val note: String?,
    @JsonProperty("created_at")
    val createdAt: LocalDateTime?,
)

