package com.exe.vocafy_BE.mapper

import com.exe.vocafy_BE.model.dto.request.PaymentMethodCreateRequest
import com.exe.vocafy_BE.model.dto.request.PaymentMethodUpdateRequest
import com.exe.vocafy_BE.model.dto.response.PaymentMethodResponse
import com.exe.vocafy_BE.model.entity.PaymentMethod

object PaymentMethodMapper {
    fun toEntity(request: PaymentMethodCreateRequest): PaymentMethod =
        PaymentMethod(
            provider = request.provider.orEmpty(),
            description = request.description,
        )

    fun applyUpdate(entity: PaymentMethod, request: PaymentMethodUpdateRequest): PaymentMethod =
        PaymentMethod(
            id = entity.id,
            provider = request.provider.orEmpty(),
            description = request.description,
            active = entity.active,
        )

    fun applyActive(entity: PaymentMethod, active: Boolean): PaymentMethod =
        PaymentMethod(
            id = entity.id,
            provider = entity.provider,
            description = entity.description,
            active = active,
        )

    fun toResponse(entity: PaymentMethod): PaymentMethodResponse =
        PaymentMethodResponse(
            id = entity.id ?: 0,
            provider = entity.provider,
            description = entity.description,
            active = entity.active,
        )
}
