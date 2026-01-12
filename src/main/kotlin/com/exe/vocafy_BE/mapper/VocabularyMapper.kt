package com.exe.vocafy_BE.mapper

import com.exe.vocafy_BE.model.dto.request.VocabularyCreateRequest
import com.exe.vocafy_BE.model.dto.request.VocabularyUpdateRequest
import com.exe.vocafy_BE.model.dto.response.VocabularyResponse
import com.exe.vocafy_BE.model.entity.Vocabulary

object VocabularyMapper {
    fun toEntity(request: VocabularyCreateRequest): Vocabulary =
        Vocabulary(
            jpKanji = request.jpKanji,
            jpKana = request.jpKana,
            jpRomaji = request.jpRomaji,
            enWord = request.enWord,
            enIpa = request.enIpa,
            meaningVi = request.meaningVi,
            meaningEn = request.meaningEn,
            meaningJp = request.meaningJp,
            note = request.note,
        )

    fun applyUpdate(entity: Vocabulary, request: VocabularyUpdateRequest): Vocabulary =
        Vocabulary(
            id = entity.id,
            jpKanji = request.jpKanji,
            jpKana = request.jpKana,
            jpRomaji = request.jpRomaji,
            enWord = request.enWord,
            enIpa = request.enIpa,
            meaningVi = request.meaningVi,
            meaningEn = request.meaningEn,
            meaningJp = request.meaningJp,
            note = request.note,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )

    fun toResponse(entity: Vocabulary): VocabularyResponse =
        VocabularyResponse(
            id = entity.id ?: 0,
            jpKanji = entity.jpKanji,
            jpKana = entity.jpKana,
            jpRomaji = entity.jpRomaji,
            enWord = entity.enWord,
            enIpa = entity.enIpa,
            meaningVi = entity.meaningVi,
            meaningEn = entity.meaningEn,
            meaningJp = entity.meaningJp,
            note = entity.note,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )
}
