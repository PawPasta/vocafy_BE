package com.exe.vocafy_BE.mapper

import com.exe.vocafy_BE.model.dto.response.ProfileResponse
import com.exe.vocafy_BE.model.entity.Profile
import java.util.UUID

object ProfileMapper {
    fun toResponse(entity: Profile): ProfileResponse =
        ProfileResponse(
            id = entity.id ?: entity.user.id ?: UUID(0, 0),
            userId = entity.user.id ?: UUID(0, 0),
            displayName = entity.displayName,
            avatarUrl = entity.avatarUrl,
            locale = entity.locale,
            timezone = entity.timezone,
        )
}
