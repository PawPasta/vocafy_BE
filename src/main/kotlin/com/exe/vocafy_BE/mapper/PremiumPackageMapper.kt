package com.exe.vocafy_BE.mapper

import com.exe.vocafy_BE.model.dto.response.PremiumPackageResponse
import com.exe.vocafy_BE.model.entity.PremiumPackage

object PremiumPackageMapper {
    fun toResponse(entity: PremiumPackage): PremiumPackageResponse =
        PremiumPackageResponse(
            id = entity.id ?: 0,
            name = entity.name,
            description = entity.description,
            price = entity.price,
            durationDays = entity.durationDays,
            active = entity.active,
            createdAt = entity.createdAt,
        )
}
