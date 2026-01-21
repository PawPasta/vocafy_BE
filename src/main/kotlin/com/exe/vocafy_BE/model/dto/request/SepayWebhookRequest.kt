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
    val transferAmount: Long?,

    val accumulated: Long?,

    @JsonProperty("referenceCode")
    val referenceCode: String?
)

/**
 * Sample payload from SePay:
 * {
 *   "id": 92704,
 *   "gateway": "Vietcombank",
 *   "transactionDate": "2023-03-25 14:02:37",
 *   "accountNumber": "0123499999",
 *   "content": "chuyen tien mua iphone",
 *   "transferType": "in",
 *   "transferAmount": 2277000,
 *   "accumulated": 19077000,
 *   "referenceCode": "MBVCB.3278907687",
 *   "description": ""
 * }
 */

