package com.exe.vocafy_BE.mapper

import com.exe.vocafy_BE.model.dto.request.FeedbackCreateRequest
import com.exe.vocafy_BE.model.dto.response.FeedbackResponse
import com.exe.vocafy_BE.model.entity.Feedback
import com.exe.vocafy_BE.model.entity.User
import java.time.LocalDateTime

object FeedbackMapper {
    fun toEntity(request: FeedbackCreateRequest, user: User): Feedback =
        Feedback(
            user = user,
            rating = request.rating ?: 0,
            title = request.title?.trim()?.ifBlank { null },
            content = request.content?.trim()?.ifBlank { null },
        )

    fun applyAdminReply(entity: Feedback, admin: User, adminReply: String): Feedback =
        Feedback(
            id = entity.id,
            user = entity.user,
            rating = entity.rating,
            title = entity.title,
            content = entity.content,
            adminReply = adminReply,
            repliedBy = admin,
            repliedAt = LocalDateTime.now(),
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )

    fun toResponse(entity: Feedback): FeedbackResponse =
        FeedbackResponse(
            id = entity.id ?: 0L,
            userId = entity.user.id ?: throw IllegalStateException("User id is null"),
            userDisplayName = entity.user.profile?.displayName,
            userEmail = entity.user.email,
            rating = entity.rating,
            title = entity.title,
            content = entity.content,
            adminReply = entity.adminReply,
            repliedByUserId = entity.repliedBy?.id,
            repliedByEmail = entity.repliedBy?.email,
            repliedAt = entity.repliedAt,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )
}
