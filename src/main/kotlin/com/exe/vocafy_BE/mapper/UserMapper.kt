package com.exe.vocafy_BE.mapper

import com.exe.vocafy_BE.model.dto.response.UserResponse
import com.exe.vocafy_BE.model.entity.User
import java.util.UUID

object UserMapper {
    fun toResponse(entity: User): UserResponse =
        UserResponse(
            id = entity.id ?: UUID(0, 0),
            email = entity.email,
            role = entity.role,
            status = entity.status,
            lastLoginAt = entity.lastLoginAt,
            lastActiveAt = entity.lastActiveAt,
            sepayCode = entity.sepayCode,
            fcmToken = entity.fcmToken,
            profile = entity.profile?.let(ProfileMapper::toResponse),
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )
}

