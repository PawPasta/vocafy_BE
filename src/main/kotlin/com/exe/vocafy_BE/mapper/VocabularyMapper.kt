package com.exe.vocafy_BE.mapper

import com.exe.vocafy_BE.model.dto.request.VocabularyCreateRequest
import com.exe.vocafy_BE.model.dto.request.VocabularyUpdateRequest
import com.exe.vocafy_BE.model.dto.response.VocabularyResponse
import com.exe.vocafy_BE.model.dto.response.VocabularyMeaningResponse
import com.exe.vocafy_BE.model.dto.response.VocabularyMediaResponse
import com.exe.vocafy_BE.model.dto.response.VocabularyTermResponse
import com.exe.vocafy_BE.model.entity.Course
import com.exe.vocafy_BE.model.entity.Vocabulary

object VocabularyMapper {
    fun toEntity(request: VocabularyCreateRequest, course: Course): Vocabulary =
        Vocabulary(
            note = request.note,
            sortOrder = request.sortOrder ?: 0,
            course = course,
        )

    fun applyUpdate(entity: Vocabulary, request: VocabularyUpdateRequest, course: Course): Vocabulary =
        Vocabulary(
            id = entity.id,
            note = request.note,
            sortOrder = request.sortOrder ?: entity.sortOrder,
            course = course,
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
            courseId = entity.course.id ?: 0,
            note = entity.note,
            sortOrder = entity.sortOrder,
            terms = terms,
            meanings = meanings,
            medias = medias,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )
}
