package com.exe.vocafy_BE.mapper

import com.exe.vocafy_BE.model.dto.request.SyllabusCreateRequest
import com.exe.vocafy_BE.model.dto.request.SyllabusUpdateRequest
import com.exe.vocafy_BE.model.dto.response.SyllabusResponse
import com.exe.vocafy_BE.model.dto.response.SyllabusTopicResponse
import com.exe.vocafy_BE.model.entity.Syllabus
import com.exe.vocafy_BE.model.entity.User

object SyllabusMapper {
    fun toEntity(request: SyllabusCreateRequest, createdBy: User?): Syllabus =
        Syllabus(
            title = request.title.orEmpty(),
            description = request.description,
            totalDays = request.totalDays ?: 0,
            languageSet = request.languageSet!!,
            visibility = request.visibility!!,
            sourceType = request.sourceType!!,
            createdBy = createdBy,
            active = request.active ?: true,
            isDeleted = false,
        )

    fun applyUpdate(entity: Syllabus, request: SyllabusUpdateRequest, createdBy: User?): Syllabus =
        Syllabus(
            id = entity.id,
            title = request.title.orEmpty(),
            description = request.description,
            totalDays = request.totalDays ?: 0,
            languageSet = request.languageSet!!,
            visibility = request.visibility!!,
            sourceType = request.sourceType!!,
            createdBy = createdBy,
            active = entity.active,
            isDeleted = entity.isDeleted,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )

    fun applyActive(entity: Syllabus, active: Boolean): Syllabus =
        Syllabus(
            id = entity.id,
            title = entity.title,
            description = entity.description,
            totalDays = entity.totalDays,
            languageSet = entity.languageSet,
            visibility = entity.visibility,
            sourceType = entity.sourceType,
            createdBy = entity.createdBy,
            active = active,
            isDeleted = entity.isDeleted,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )

    fun toResponse(
        entity: Syllabus,
        topics: List<SyllabusTopicResponse>? = null,
        includeSensitive: Boolean = true,
    ): SyllabusResponse =
        SyllabusResponse(
            id = entity.id ?: 0,
            title = entity.title,
            description = entity.description,
            totalDays = entity.totalDays,
            languageSet = entity.languageSet,
            visibility = entity.visibility,
            sourceType = entity.sourceType,
            createdByUserId = entity.createdBy?.id?.toString(),
            active = if (includeSensitive) entity.active else null,
            isDeleted = if (includeSensitive) entity.isDeleted else null,
            createdAt = entity.createdAt,
            updatedAt = if (includeSensitive) entity.updatedAt else null,
            topics = topics,
        )
}
