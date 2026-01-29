package com.exe.vocafy_BE.mapper

import com.exe.vocafy_BE.model.dto.request.CourseCreateRequest
import com.exe.vocafy_BE.model.dto.request.CourseUpdateRequest
import com.exe.vocafy_BE.model.dto.response.CourseResponse
import com.exe.vocafy_BE.model.entity.Course
import com.exe.vocafy_BE.model.entity.User

object CourseMapper {
    fun toEntity(request: CourseCreateRequest, createdBy: User): Course =
        Course(
            title = request.title.orEmpty(),
            description = request.description,
            sortOrder = request.sortOrder ?: 0,
            syllabusTopic = null,
            createdBy = createdBy,
            isActive = true,
            isDeleted = false,
        )

    fun applyUpdate(entity: Course, request: CourseUpdateRequest): Course =
        Course(
            id = entity.id,
            title = request.title.orEmpty(),
            description = request.description,
            sortOrder = request.sortOrder ?: entity.sortOrder,
            syllabusTopic = entity.syllabusTopic,
            createdBy = entity.createdBy,
            isActive = entity.isActive,
            isDeleted = entity.isDeleted,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )

    fun toResponse(entity: Course): CourseResponse =
        CourseResponse(
            id = entity.id ?: 0,
            topicId = entity.syllabusTopic?.id,
            createdByUserId = entity.createdBy.id?.toString(),
            title = entity.title,
            description = entity.description,
            sortOrder = entity.sortOrder,
            isActive = entity.isActive,
            isDeleted = entity.isDeleted,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )
}
