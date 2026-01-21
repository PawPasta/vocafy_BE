package com.exe.vocafy_BE.model.dto.request

import com.fasterxml.jackson.annotation.JsonProperty

data class SepayWebhookRequest(
    val id: Long?,
    val gateway: String?,
    @JsonProperty("transactionDate")
    val transactionDate: String?,
    @JsonProperty("accountNumber")
    val accountNumber: String?,
    @JsonProperty("subAccount")
    val subAccount: String?,
    val code: String?,
    val content: String?,
    @JsonProperty("transferType")
    val transferType: String?,
    val description: String?,
    @JsonProperty("transferAmount")
    val transferAmount: Int?,
    val accumulated: Long?,
    val referenceCode: String?,
)
