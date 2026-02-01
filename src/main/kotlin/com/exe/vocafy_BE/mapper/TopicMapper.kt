package com.exe.vocafy_BE.mapper

import com.exe.vocafy_BE.model.dto.request.TopicCreateRequest
import com.exe.vocafy_BE.model.dto.request.TopicUpdateRequest
import com.exe.vocafy_BE.model.dto.response.CourseResponse
import com.exe.vocafy_BE.model.dto.response.TopicResponse
import com.exe.vocafy_BE.model.entity.Topic
import com.exe.vocafy_BE.model.entity.User

object TopicMapper {
    fun toEntity(request: TopicCreateRequest, createdBy: User): Topic =
        Topic(
            createdBy = createdBy,
            title = request.title.orEmpty(),
            description = request.description,
            totalDays = request.totalDays ?: 1,
            sortOrder = request.sortOrder ?: 0,
            isActive = true,
            isDeleted = false,
        )

    fun applyUpdate(entity: Topic, request: TopicUpdateRequest): Topic =
        Topic(
            id = entity.id,
            createdBy = entity.createdBy,
            title = request.title.orEmpty(),
            description = request.description,
            totalDays = request.totalDays ?: entity.totalDays,
            sortOrder = request.sortOrder ?: entity.sortOrder,
            isActive = entity.isActive,
            isDeleted = entity.isDeleted,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )

    fun toResponse(entity: Topic, courses: List<CourseResponse>? = null, syllabusId: Long? = null): TopicResponse =
        TopicResponse(
            id = entity.id ?: 0,
            syllabusId = syllabusId,
            createdByUserId = entity.createdBy.id?.toString(),
            title = entity.title,
            description = entity.description,
            totalDays = entity.totalDays,
            sortOrder = entity.sortOrder,
            isActive = entity.isActive,
            isDeleted = entity.isDeleted,
            courses = courses,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )
}
