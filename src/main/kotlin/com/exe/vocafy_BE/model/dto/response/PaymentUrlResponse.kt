package com.exe.vocafy_BE.model.dto.response

import com.fasterxml.jackson.annotation.JsonProperty

data class PaymentUrlResponse(
    val url: String,
    val amount: Int,
    @JsonProperty("ref1")
    val ref1: String,
)
