package com.exe.vocafy_BE.mapper

import com.exe.vocafy_BE.model.dto.request.CategoryCreateRequest
import com.exe.vocafy_BE.model.dto.request.CategoryUpdateRequest
import com.exe.vocafy_BE.model.dto.response.CategoryResponse
import com.exe.vocafy_BE.model.entity.Category

object CategoryMapper {
    fun toEntity(request: CategoryCreateRequest): Category =
        Category(
            name = request.name!!,
            description = request.description
        )

    fun applyUpdate(entity: Category, request: CategoryUpdateRequest): Category =
        Category(
            id = entity.id,
            name = request.name ?: entity.name,
            description = request.description ?: entity.description,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )

    fun toResponse(entity: Category): CategoryResponse =
        CategoryResponse(
            id = entity.id ?: 0,
            name = entity.name,
            description = entity.description,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
}
