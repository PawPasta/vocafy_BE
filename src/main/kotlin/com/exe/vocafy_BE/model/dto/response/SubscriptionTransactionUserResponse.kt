package com.exe.vocafy_BE.model.dto.response

import java.util.UUID

data class SubscriptionTransactionUserResponse(
    val id: UUID,
    val email: String,
)

