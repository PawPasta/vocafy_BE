package com.exe.vocafy_BE.implement

import com.exe.vocafy_BE.handler.BaseException
import com.exe.vocafy_BE.mapper.VocabularyMapper
import com.exe.vocafy_BE.model.dto.request.VocabularyCreateRequest
import com.exe.vocafy_BE.model.dto.request.VocabularyQuickCreateRequest
import com.exe.vocafy_BE.model.dto.request.VocabularyUpdateRequest
import com.exe.vocafy_BE.model.dto.response.PageResponse
import com.exe.vocafy_BE.model.dto.response.ServiceResult
import com.exe.vocafy_BE.model.dto.response.VocabularyMeaningResponse
import com.exe.vocafy_BE.model.dto.response.VocabularyMediaResponse
import com.exe.vocafy_BE.model.dto.response.VocabularyResponse
import com.exe.vocafy_BE.model.dto.response.VocabularyTermResponse
import com.exe.vocafy_BE.enum.Role
import com.exe.vocafy_BE.model.entity.VocabularyMeaning
import com.exe.vocafy_BE.model.entity.VocabularyMedia
import com.exe.vocafy_BE.model.entity.VocabularyTerm
import com.exe.vocafy_BE.model.entity.VocabularyExample
import com.exe.vocafy_BE.model.entity.VocabularyExampleTranslation
import com.exe.vocafy_BE.repo.CourseVocabularyLinkRepository
import com.exe.vocafy_BE.repo.VocabularyExampleRepository
import com.exe.vocafy_BE.repo.VocabularyExampleTranslationRepository
import com.exe.vocafy_BE.repo.VocabularyMeaningRepository
import com.exe.vocafy_BE.repo.VocabularyMediaRepository
import com.exe.vocafy_BE.repo.VocabularyRepository
import com.exe.vocafy_BE.repo.VocabularyTermRepository
import com.exe.vocafy_BE.service.VocabularyService
import com.exe.vocafy_BE.util.SecurityUtil
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class VocabularyServiceImpl(
    private val securityUtil: SecurityUtil,
    private val vocabularyRepository: VocabularyRepository,
    private val vocabularyTermRepository: VocabularyTermRepository,
    private val vocabularyMeaningRepository: VocabularyMeaningRepository,
    private val vocabularyExampleRepository: VocabularyExampleRepository,
    private val vocabularyExampleTranslationRepository: VocabularyExampleTranslationRepository,
    private val vocabularyMediaRepository: VocabularyMediaRepository,
    private val courseVocabularyLinkRepository: CourseVocabularyLinkRepository,
) : VocabularyService {

    @Transactional
    override fun create(request: VocabularyCreateRequest): ServiceResult<VocabularyResponse> {
        val createdBy = securityUtil.getCurrentUser()
        val saved = vocabularyRepository.save(VocabularyMapper.toEntity(request, createdBy))
        saveChildren(saved.id ?: 0, request)
        return ServiceResult(
            message = "Created",
            result = buildResponse(saved),
        )
    }

    @Transactional
    override fun quickCreate(request: VocabularyQuickCreateRequest): ServiceResult<VocabularyResponse> {
        val createdBy = securityUtil.getCurrentUser()
        val saved = vocabularyRepository.save(
            com.exe.vocafy_BE.model.entity.Vocabulary(
                note = request.note,
                sortOrder = request.sortOrder ?: 0,
                createdBy = createdBy,
                isActive = true,
                isDeleted = false,
            )
        )

        val languageCode = request.languageCode!!
        vocabularyTermRepository.save(
            VocabularyTerm(
                vocabulary = saved,
                languageCode = languageCode,
                scriptType = request.scriptType!!,
                textValue = request.term.orEmpty(),
            )
        )

        if (!request.meaningText.isNullOrBlank()) {
            val pos = request.partOfSpeech
                ?: throw BaseException.BadRequestException("'part_of_speech' is required when meaning_text is provided")
            val meaning = vocabularyMeaningRepository.save(
                VocabularyMeaning(
                    vocabulary = saved,
                    languageCode = languageCode,
                    meaningText = request.meaningText,
                    exampleSentence = request.exampleSentence,
                    exampleTranslation = request.exampleTranslation,
                    partOfSpeech = pos,
                    senseOrder = 1,
                )
            )
            saveExampleFromInput(
                vocabId = saved.id ?: 0L,
                sentenceLanguageCode = request.languageCode,
                sentenceText = request.exampleSentence,
                translationLanguageCode = meaning.languageCode,
                translationText = request.exampleTranslation,
                sortOrder = meaning.senseOrder ?: 1,
            )
        }

        return ServiceResult(
            message = "Created",
            result = buildResponse(saved),
        )
    }

    @Transactional(readOnly = true)
    override fun getById(id: Long): ServiceResult<VocabularyResponse> {
        val entity = vocabularyRepository.findById(id)
            .orElseThrow { BaseException.NotFoundException("Vocabulary not found") }
        return ServiceResult(
            message = "Ok",
            result = buildResponse(entity),
        )
    }

    @Transactional(readOnly = true)
    override fun list(pageable: Pageable): ServiceResult<PageResponse<VocabularyResponse>> {
        val page = vocabularyRepository.findAll(pageable)
        val items = page.content.map { buildResponse(it) }
        return ServiceResult(
            message = "Ok",
            result = PageResponse(
                content = items,
                page = page.number,
                size = page.size,
                totalElements = page.totalElements,
                totalPages = page.totalPages,
                isFirst = page.isFirst,
                isLast = page.isLast,
            ),
        )
    }

    @Transactional(readOnly = true)
    override fun listByCourseId(courseId: Long, pageable: Pageable): ServiceResult<PageResponse<VocabularyResponse>> {
        val page = courseVocabularyLinkRepository.findVocabulariesByCourseId(courseId, pageable)
        val items = page.content.map { buildResponse(it, courseId) }
        return ServiceResult(
            message = "Ok",
            result = PageResponse(
                content = items,
                page = page.number,
                size = page.size,
                totalElements = page.totalElements,
                totalPages = page.totalPages,
                isFirst = page.isFirst,
                isLast = page.isLast,
            ),
        )
    }

    @Transactional(readOnly = true)
    override fun listByUserId(userId: UUID, pageable: Pageable): ServiceResult<PageResponse<VocabularyResponse>> { val requester = securityUtil.getCurrentUser()
        if (requester.role != Role.ADMIN && requester.role != Role.MANAGER) {
            throw BaseException.ForbiddenException("Forbidden")
        }
        val page = vocabularyRepository.findAllByCreatedById(userId, pageable)
        val items = page.content.map { buildResponse(it) }
        return ServiceResult(
            message = "Ok",
            result = PageResponse(
                content = items,
                page = page.number,
                size = page.size,
                totalElements = page.totalElements,
                totalPages = page.totalPages,
                isFirst = page.isFirst,
                isLast = page.isLast,
            ),
        )
    }

    @Transactional(readOnly = true)
    override fun listMine(pageable: Pageable): ServiceResult<PageResponse<VocabularyResponse>> {
        val user = securityUtil.getCurrentUser()
        val userId = user.id ?: throw BaseException.NotFoundException("User not found")
        val page = vocabularyRepository.findAllByCreatedById(userId, pageable)
        val items = page.content.map { buildResponse(it) }
        return ServiceResult(
            message = "Ok",
            result = PageResponse(
                content = items,
                page = page.number,
                size = page.size,
                totalElements = page.totalElements,
                totalPages = page.totalPages,
                isFirst = page.isFirst,
                isLast = page.isLast,
            ),
        )
    }

    @Transactional
    override fun update(id: Long, request: VocabularyUpdateRequest): ServiceResult<VocabularyResponse> {
        val requester = securityUtil.getCurrentUser()
        if (requester.role != Role.ADMIN && requester.role != Role.MANAGER) {
            throw BaseException.ForbiddenException("Forbidden")
        }
        val entity = vocabularyRepository.findById(id)
            .orElseThrow { BaseException.NotFoundException("Vocabulary not found") }
        val updated = vocabularyRepository.save(VocabularyMapper.applyUpdate(entity, request))
        replaceChildren(updated.id ?: 0, request)
        return ServiceResult(
            message = "Updated",
            result = buildResponse(updated),
        )
    }

    @Transactional
    override fun updateMine(id: Long, request: VocabularyUpdateRequest): ServiceResult<VocabularyResponse> {
        val requester = securityUtil.getCurrentUser()
        val requesterId = requester.id ?: throw BaseException.NotFoundException("User not found")
        val entity = vocabularyRepository.findById(id)
            .orElseThrow { BaseException.NotFoundException("Vocabulary not found") }
        val ownerId = entity.createdBy.id ?: throw BaseException.NotFoundException("Vocabulary owner not found")
        if (ownerId != requesterId) {
            throw BaseException.ForbiddenException("Forbidden")
        }

        val updated = vocabularyRepository.save(VocabularyMapper.applyUpdate(entity, request))
        replaceChildren(updated.id ?: 0, request)
        return ServiceResult(
            message = "Updated",
            result = buildResponse(updated),
        )
    }

    @Transactional
    override fun delete(id: Long): ServiceResult<Unit> {
        val requester = securityUtil.getCurrentUser()
        if (requester.role != Role.ADMIN && requester.role != Role.MANAGER) {
            throw BaseException.ForbiddenException("Forbidden")
        }
        val entity = vocabularyRepository.findById(id)
            .orElseThrow { BaseException.NotFoundException("Vocabulary not found") }

        vocabularyTermRepository.deleteAllByVocabularyId(id)
        vocabularyMeaningRepository.deleteAllByVocabularyId(id)
        deleteExamplesByVocabularyId(id)
        vocabularyMediaRepository.deleteAllByVocabularyId(id)
        courseVocabularyLinkRepository.deleteAllByVocabularyId(id)

        vocabularyRepository.delete(entity)

        return ServiceResult(
            message = "Deleted",
            result = Unit,
        )
    }

    @Transactional
    override fun deleteMine(id: Long): ServiceResult<Unit> {
        val requester = securityUtil.getCurrentUser()
        val requesterId = requester.id ?: throw BaseException.NotFoundException("User not found")
        val entity = vocabularyRepository.findById(id)
            .orElseThrow { BaseException.NotFoundException("Vocabulary not found") }
        val ownerId = entity.createdBy.id ?: throw BaseException.NotFoundException("Vocabulary owner not found")
        if (ownerId != requesterId) {
            throw BaseException.ForbiddenException("Forbidden")
        }

        vocabularyTermRepository.deleteAllByVocabularyId(id)
        vocabularyMeaningRepository.deleteAllByVocabularyId(id)
        deleteExamplesByVocabularyId(id)
        vocabularyMediaRepository.deleteAllByVocabularyId(id)
        courseVocabularyLinkRepository.deleteAllByVocabularyId(id)

        vocabularyRepository.delete(entity)

        return ServiceResult(
            message = "Deleted",
            result = Unit,
        )
    }

    private fun buildResponse(
        entity: com.exe.vocafy_BE.model.entity.Vocabulary,
        courseId: Long? = null,
    ): VocabularyResponse {
        val vocabId = entity.id ?: 0
        val terms = vocabularyTermRepository.findAllByVocabularyIdOrderByIdAsc(vocabId).map {
            VocabularyTermResponse(
                id = it.id ?: 0,
                languageCode = it.languageCode,
                scriptType = it.scriptType,
                textValue = it.textValue,
                extraMeta = it.extraMeta,
                createdAt = it.createdAt,
                updatedAt = it.updatedAt,
            )
        }
        val meanings = vocabularyMeaningRepository.findAllByVocabularyIdOrderBySenseOrderAscIdAsc(vocabId).map {
            VocabularyMeaningResponse(
                id = it.id ?: 0,
                languageCode = it.languageCode,
                meaningText = it.meaningText,
                exampleSentence = it.exampleSentence,
                exampleTranslation = it.exampleTranslation,
                partOfSpeech = it.partOfSpeech,
                senseOrder = it.senseOrder,
                createdAt = it.createdAt,
                updatedAt = it.updatedAt,
            )
        }
        val medias = vocabularyMediaRepository.findAllByVocabularyIdOrderByIdAsc(vocabId).map {
            VocabularyMediaResponse(
                id = it.id ?: 0,
                mediaType = it.mediaType,
                url = it.url,
                meta = it.meta,
                createdAt = it.createdAt,
                updatedAt = it.updatedAt,
            )
        }
        val resolvedCourseId = courseId ?: resolveCourseId(vocabId)
        return VocabularyMapper.toResponse(entity, terms, meanings, medias, resolvedCourseId)
    }

    private fun saveChildren(vocabId: Long, request: VocabularyCreateRequest) {
        val terms = request.terms.orEmpty().map {
            VocabularyTerm(
                vocabulary = vocabularyRepository.getReferenceById(vocabId),
                languageCode = it.languageCode!!,
                scriptType = it.scriptType!!,
                textValue = it.textValue.orEmpty(),
                extraMeta = it.extraMeta,
            )
        }
        val meanings = request.meanings.orEmpty().map {
            VocabularyMeaning(
                vocabulary = vocabularyRepository.getReferenceById(vocabId),
                languageCode = it.languageCode!!,
                meaningText = it.meaningText.orEmpty(),
                exampleSentence = it.exampleSentence,
                exampleTranslation = it.exampleTranslation,
                partOfSpeech = it.partOfSpeech!!,
                senseOrder = it.senseOrder,
            )
        }
        val medias = request.medias.orEmpty().map {
            VocabularyMedia(
                vocabulary = vocabularyRepository.getReferenceById(vocabId),
                mediaType = it.mediaType!!,
                url = it.url.orEmpty(),
                meta = it.meta,
            )
        }
        if (terms.isNotEmpty()) {
            vocabularyTermRepository.saveAll(terms)
        }
        if (meanings.isNotEmpty()) {
            vocabularyMeaningRepository.saveAll(meanings)
            saveExamplesFromMeanings(
                vocabId = vocabId,
                sentenceLanguageCode = terms.firstOrNull()?.languageCode,
                meanings = meanings,
            )
        }
        if (medias.isNotEmpty()) {
            vocabularyMediaRepository.saveAll(medias)
        }
    }

    private fun replaceChildren(vocabId: Long, request: VocabularyUpdateRequest) {
        if (request.terms != null) {
            vocabularyTermRepository.deleteAllByVocabularyId(vocabId)
            val terms = request.terms.map {
                VocabularyTerm(
                    vocabulary = vocabularyRepository.getReferenceById(vocabId),
                    languageCode = it.languageCode!!,
                    scriptType = it.scriptType!!,
                    textValue = it.textValue.orEmpty(),
                    extraMeta = it.extraMeta,
                )
            }
            vocabularyTermRepository.saveAll(terms)
        }
        if (request.meanings != null) {
            vocabularyMeaningRepository.deleteAllByVocabularyId(vocabId)
            val meanings = request.meanings.map {
                VocabularyMeaning(
                    vocabulary = vocabularyRepository.getReferenceById(vocabId),
                    languageCode = it.languageCode!!,
                    meaningText = it.meaningText.orEmpty(),
                    exampleSentence = it.exampleSentence,
                    exampleTranslation = it.exampleTranslation,
                    partOfSpeech = it.partOfSpeech!!,
                    senseOrder = it.senseOrder,
                )
            }
            vocabularyMeaningRepository.saveAll(meanings)
            val sentenceLanguageCode = request.terms?.firstOrNull()?.languageCode
                ?: vocabularyTermRepository.findAllByVocabularyIdOrderByIdAsc(vocabId).firstOrNull()?.languageCode
            deleteExamplesByVocabularyId(vocabId)
            saveExamplesFromMeanings(
                vocabId = vocabId,
                sentenceLanguageCode = sentenceLanguageCode,
                meanings = meanings,
            )
        }
        if (request.medias != null) {
            vocabularyMediaRepository.deleteAllByVocabularyId(vocabId)
            val medias = request.medias.map {
                VocabularyMedia(
                    vocabulary = vocabularyRepository.getReferenceById(vocabId),
                    mediaType = it.mediaType!!,
                    url = it.url.orEmpty(),
                    meta = it.meta,
                )
            }
            vocabularyMediaRepository.saveAll(medias)
        }
    }

    private fun resolveCourseId(vocabId: Long): Long? {
        return courseVocabularyLinkRepository.findFirstByVocabularyIdOrderByIdAsc(vocabId)
            ?.course
            ?.id
    }

    private fun saveExamplesFromMeanings(
        vocabId: Long,
        sentenceLanguageCode: com.exe.vocafy_BE.enum.LanguageCode?,
        meanings: List<VocabularyMeaning>,
    ) {
        val sentenceLanguage = sentenceLanguageCode ?: return
        meanings.forEachIndexed { index, meaning ->
            saveExampleFromInput(
                vocabId = vocabId,
                sentenceLanguageCode = sentenceLanguage,
                sentenceText = meaning.exampleSentence,
                translationLanguageCode = meaning.languageCode,
                translationText = meaning.exampleTranslation,
                sortOrder = index + 1,
            )
        }
    }

    private fun saveExampleFromInput(
        vocabId: Long,
        sentenceLanguageCode: com.exe.vocafy_BE.enum.LanguageCode?,
        sentenceText: String?,
        translationLanguageCode: com.exe.vocafy_BE.enum.LanguageCode?,
        translationText: String?,
        sortOrder: Int,
    ) {
        if (sentenceLanguageCode == null || sentenceText.isNullOrBlank()) {
            return
        }
        val example = vocabularyExampleRepository.save(
            VocabularyExample(
                vocabulary = vocabularyRepository.getReferenceById(vocabId),
                languageCode = sentenceLanguageCode,
                sentenceText = sentenceText,
                sortOrder = sortOrder,
            )
        )
        val exampleId = example.id ?: return
        if (translationLanguageCode == null || translationText.isNullOrBlank()) {
            return
        }
        vocabularyExampleTranslationRepository.save(
            VocabularyExampleTranslation(
                vocabularyExample = vocabularyExampleRepository.getReferenceById(exampleId),
                languageCode = translationLanguageCode,
                translationText = translationText,
            )
        )
    }

    private fun deleteExamplesByVocabularyId(vocabId: Long) {
        val exampleIds = vocabularyExampleRepository.findAllByVocabularyIdOrderBySortOrderAscIdAsc(vocabId)
            .mapNotNull { it.id }
        if (exampleIds.isNotEmpty()) {
            vocabularyExampleTranslationRepository.deleteAllByVocabularyExampleIdIn(exampleIds)
        }
        vocabularyExampleRepository.deleteAllByVocabularyId(vocabId)
    }
}
