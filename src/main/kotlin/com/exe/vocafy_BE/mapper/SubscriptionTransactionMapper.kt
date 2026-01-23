package com.exe.vocafy_BE.mapper

import com.exe.vocafy_BE.model.dto.response.SubscriptionTransactionResponse
import com.exe.vocafy_BE.model.dto.response.SubscriptionTransactionUserResponse
import com.exe.vocafy_BE.model.entity.SubscriptionTransaction
import java.util.UUID

object SubscriptionTransactionMapper {
    fun toResponse(entity: SubscriptionTransaction): SubscriptionTransactionResponse =
        SubscriptionTransactionResponse(
            id = entity.id ?: UUID(0, 0),
            user = SubscriptionTransactionUserResponse(
                id = entity.user.id ?: UUID(0, 0),
                email = entity.user.email,
            ),
            paymentMethodId = entity.paymentMethod.id ?: 0,
            amount = entity.amount,
            status = entity.status,
            note = entity.note,
            createdAt = entity.createdAt,
        )
}

