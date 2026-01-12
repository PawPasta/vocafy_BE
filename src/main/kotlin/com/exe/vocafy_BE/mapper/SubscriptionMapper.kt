package com.exe.vocafy_BE.mapper

import com.exe.vocafy_BE.model.dto.response.SubscriptionResponse
import com.exe.vocafy_BE.model.entity.Subscription

object SubscriptionMapper {
    fun toResponse(entity: Subscription): SubscriptionResponse =
        SubscriptionResponse(
            plan = entity.plan,
            startAt = entity.startAt,
            endAt = entity.endAt,
            updatedAt = entity.updatedAt,
        )
}
