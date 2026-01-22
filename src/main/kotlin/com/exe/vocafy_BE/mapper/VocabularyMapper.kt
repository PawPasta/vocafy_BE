package com.exe.vocafy_BE.mapper

import com.exe.vocafy_BE.model.dto.request.VocabularyCreateRequest
import com.exe.vocafy_BE.model.dto.request.VocabularyUpdateRequest
import com.exe.vocafy_BE.model.dto.response.VocabularyResponse
import com.exe.vocafy_BE.model.dto.response.VocabularyMeaningResponse
import com.exe.vocafy_BE.model.dto.response.VocabularyMediaResponse
import com.exe.vocafy_BE.model.dto.response.VocabularyTermResponse
import com.exe.vocafy_BE.model.entity.Vocabulary

object VocabularyMapper {
    fun toEntity(request: VocabularyCreateRequest): Vocabulary =
        Vocabulary(
            note = request.note,
            sortOrder = request.sortOrder ?: 0,
            course = null,
            isActive = true,
            isDeleted = false,
        )

    fun applyUpdate(entity: Vocabulary, request: VocabularyUpdateRequest): Vocabulary =
        Vocabulary(
            id = entity.id,
            note = request.note,
            sortOrder = request.sortOrder ?: entity.sortOrder,
            course = entity.course,
            isActive = entity.isActive,
            isDeleted = entity.isDeleted,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )

    fun toResponse(
        entity: Vocabulary,
        terms: List<VocabularyTermResponse> = emptyList(),
        meanings: List<VocabularyMeaningResponse> = emptyList(),
        medias: List<VocabularyMediaResponse> = emptyList(),
    ): VocabularyResponse =
        VocabularyResponse(
            id = entity.id ?: 0,
            courseId = entity.course?.id,
            note = entity.note,
            sortOrder = entity.sortOrder,
            isActive = entity.isActive,
            isDeleted = entity.isDeleted,
            terms = terms,
            meanings = meanings,
            medias = medias,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )
}
