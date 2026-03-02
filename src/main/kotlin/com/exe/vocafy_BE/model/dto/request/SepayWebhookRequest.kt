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
 *   "id": 43836758,
 *   "gateway": "TPBank",
 *   "transactionDate": "2026-03-02 17:46:08",
 *   "accountNumber": "07701221901",
 *   "subAccount": null,
 *   "code": null,
 *   "content": "QR - VYC591A600B671",
 *   "transferType": "in",
 *   "description": "BankAPINotify QR - VYC591A600B671",
 *   "transferAmount": 79000,
 *   "referenceCode": "272V602260613142",
 *   "accumulated": 158001
 * }
 */

