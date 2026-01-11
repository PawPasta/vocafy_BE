package com.exe.vocafy_BE.mapper

import com.exe.vocafy_BE.model.dto.request.CourseCreateRequest
import com.exe.vocafy_BE.model.dto.request.CourseUpdateRequest
import com.exe.vocafy_BE.model.dto.response.CourseResponse
import com.exe.vocafy_BE.model.entity.Course
import com.exe.vocafy_BE.model.entity.User

object CourseMapper {
    fun toEntity(request: CourseCreateRequest, createdBy: User?): Course =
        Course(
            title = request.title.orEmpty(),
            description = request.description,
            createdBy = createdBy,
        )

    fun applyUpdate(entity: Course, request: CourseUpdateRequest, createdBy: User?): Course =
        Course(
            id = entity.id,
            title = request.title.orEmpty(),
            description = request.description,
            createdBy = createdBy,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )

    fun toResponse(entity: Course): CourseResponse =
        CourseResponse(
            id = entity.id ?: 0,
            title = entity.title,
            description = entity.description,
            createdByUserId = entity.createdBy?.id?.toString(),
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )
}
