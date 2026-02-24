package com.exe.vocafy_BE.mapper

import com.exe.vocafy_BE.enum.LanguageCode
import com.exe.vocafy_BE.enum.LanguageSet
import com.exe.vocafy_BE.model.dto.request.SyllabusCreateRequest
import com.exe.vocafy_BE.model.dto.request.SyllabusUpdateRequest
import com.exe.vocafy_BE.model.dto.response.SyllabusResponse
import com.exe.vocafy_BE.model.dto.response.SyllabusTopicResponse
import com.exe.vocafy_BE.model.entity.Category
import com.exe.vocafy_BE.model.entity.Syllabus
import com.exe.vocafy_BE.model.entity.User

object SyllabusMapper {
    fun toEntity(
        request: SyllabusCreateRequest,
        createdBy: User?,
        category: Category?,
        languageSet: LanguageSet,
        studyLanguage: LanguageCode,
    ): Syllabus =
        Syllabus(
            title = request.title.orEmpty(),
            description = request.description,
            imageBackGroud = request.imageBackGroud,
            imageIcon = request.imageIcon,
            totalDays = request.totalDays ?: 0,
            languageSet = languageSet,
            studyLanguage = studyLanguage,
            visibility = request.visibility!!,
            sourceType = request.sourceType!!,
            createdBy = createdBy,
            active = request.active ?: true,
            isDeleted = false,
            category = category
        )

    fun applyUpdate(
        entity: Syllabus,
        request: SyllabusUpdateRequest,
        createdBy: User?,
        category: Category?,
        languageSet: LanguageSet,
        studyLanguage: LanguageCode,
    ): Syllabus =
        Syllabus(
            id = entity.id,
            title = request.title.orEmpty(),
            description = request.description,
            imageBackGroud = request.imageBackGroud,
            imageIcon = request.imageIcon,
            totalDays = request.totalDays ?: 0,
            languageSet = languageSet,
            studyLanguage = studyLanguage,
            visibility = request.visibility!!,
            sourceType = request.sourceType!!,
            createdBy = createdBy,
            active = entity.active,
            isDeleted = entity.isDeleted,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            category = category
        )

    fun applyActive(entity: Syllabus, active: Boolean): Syllabus =
        Syllabus(
            id = entity.id,
            title = entity.title,
            description = entity.description,
            imageBackGroud = entity.imageBackGroud,
            imageIcon = entity.imageIcon,
            totalDays = entity.totalDays,
            languageSet = entity.languageSet,
            studyLanguage = entity.studyLanguage,
            visibility = entity.visibility,
            sourceType = entity.sourceType,
            createdBy = entity.createdBy,
            active = active,
            isDeleted = entity.isDeleted,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            category = entity.category
        )

    fun toResponse(
        entity: Syllabus,
        topics: List<SyllabusTopicResponse>? = null,
        includeSensitive: Boolean = true,
        targetLanguages: List<LanguageCode>? = null,
    ): SyllabusResponse =
        run {
            val studyLanguage = entity.studyLanguage ?: defaultStudyLanguage(entity.languageSet)
            val resolvedTargets = (targetLanguages ?: defaultTargetLanguages(entity.languageSet, studyLanguage))
                .distinct()
                .filter { it != studyLanguage }
                .ifEmpty { defaultTargetLanguages(entity.languageSet, studyLanguage) }

            SyllabusResponse(
                id = entity.id ?: 0,
                title = entity.title,
                description = entity.description,
                imageBackGroud = entity.imageBackGroud,
                imageIcon = entity.imageIcon,
                totalDays = entity.totalDays,
                languageSet = entity.languageSet,
                studyLanguage = studyLanguage,
                targetLanguages = resolvedTargets,
                visibility = entity.visibility,
                sourceType = entity.sourceType,
                createdByUserId = entity.createdBy?.id?.toString(),
                active = if (includeSensitive) entity.active else null,
                isDeleted = if (includeSensitive) entity.isDeleted else null,
                createdAt = entity.createdAt,
                updatedAt = if (includeSensitive) entity.updatedAt else null,
                topics = topics,
                categoryName = entity.category?.name
            )
        }

    private fun defaultStudyLanguage(languageSet: LanguageSet): LanguageCode =
        when (languageSet) {
            LanguageSet.EN_JP -> LanguageCode.JA
            LanguageSet.EN_VI -> LanguageCode.VI
            LanguageSet.JP_VI -> LanguageCode.VI
            LanguageSet.EN_JP_VI -> LanguageCode.JA
        }

    private fun defaultTargetLanguages(languageSet: LanguageSet, studyLanguage: LanguageCode): List<LanguageCode> =
        when (languageSet) {
            LanguageSet.EN_JP -> listOf(LanguageCode.EN, LanguageCode.JA)
            LanguageSet.EN_VI -> listOf(LanguageCode.EN, LanguageCode.VI)
            LanguageSet.JP_VI -> listOf(LanguageCode.JA, LanguageCode.VI)
            LanguageSet.EN_JP_VI -> listOf(LanguageCode.EN, LanguageCode.JA, LanguageCode.VI)
        }.filter { it != studyLanguage }
}
