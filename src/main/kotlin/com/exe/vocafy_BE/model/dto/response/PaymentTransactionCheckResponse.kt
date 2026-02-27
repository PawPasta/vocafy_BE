package com.exe.vocafy_BE.model.dto.response

import com.exe.vocafy_BE.enum.SubscriptionPlan
import com.exe.vocafy_BE.enum.SubscriptionTransactionStatus
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

data class PaymentTransactionCheckResponse(
    @JsonProperty("is_registration_successful")
    val isRegistrationSuccessful: Boolean,
    @JsonProperty("payment_status")
    val paymentStatus: String,
    @JsonProperty("subscription_plan")
    val subscriptionPlan: SubscriptionPlan?,
    @JsonProperty("subscription_end_at")
    val subscriptionEndAt: LocalDate?,
    @JsonProperty("latest_transaction_status")
    val latestTransactionStatus: SubscriptionTransactionStatus?,
    @JsonProperty("latest_transaction_amount")
    val latestTransactionAmount: Long?,
)
