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
import com.exe.vocafy_BE.model.entity.VocabularyQuestion
import com.exe.vocafy_BE.model.entity.VocabularyTerm
import com.exe.vocafy_BE.model.entity.User
import com.exe.vocafy_BE.repo.CourseVocabularyLinkRepository
import com.exe.vocafy_BE.repo.VocabularyMeaningRepository
import com.exe.vocafy_BE.repo.VocabularyMediaRepository
import com.exe.vocafy_BE.repo.VocabularyQuestionRepository
import com.exe.vocafy_BE.repo.VocabularyRepository
import com.exe.vocafy_BE.repo.VocabularyTermRepository
import com.exe.vocafy_BE.repo.UserRepository
import com.exe.vocafy_BE.service.VocabularyService
import org.springframework.data.domain.Pageable
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class VocabularyServiceImpl(
    private val vocabularyRepository: VocabularyRepository,
    private val vocabularyTermRepository: VocabularyTermRepository,
    private val vocabularyMeaningRepository: VocabularyMeaningRepository,
    private val vocabularyMediaRepository: VocabularyMediaRepository,
    private val vocabularyQuestionRepository: VocabularyQuestionRepository,
    private val courseVocabularyLinkRepository: CourseVocabularyLinkRepository,
    private val userRepository: UserRepository,
) : VocabularyService {

    @Transactional
    override fun create(request: VocabularyCreateRequest): ServiceResult<VocabularyResponse> {
        val createdBy = currentUser()
        val saved = vocabularyRepository.save(VocabularyMapper.toEntity(request, createdBy))
        saveChildren(saved.id ?: 0, request)
        return ServiceResult(
            message = "Created",
            result = buildResponse(saved),
        )
    }

    @Transactional
    override fun quickCreate(request: VocabularyQuickCreateRequest): ServiceResult<VocabularyResponse> {
        val createdBy = currentUser()
        val saved = vocabularyRepository.save(
            com.exe.vocafy_BE.model.entity.Vocabulary(
                note = request.note,
                sortOrder = request.sortOrder ?: 0,
                createdBy = createdBy,
                isActive = true,
                isDeleted = false,
            )
        )

        val vocabId = saved.id ?: 0
        val term = vocabularyTermRepository.save(
            VocabularyTerm(
                vocabulary = saved,
                languageCode = request.languageCode!!,
                scriptType = request.scriptType!!,
                textValue = request.term.orEmpty(),
            )
        )

        val meaning = if (!request.meaningText.isNullOrBlank()) {
            val pos = request.partOfSpeech
                ?: throw BaseException.BadRequestException("'part_of_speech' is required when meaning_text is provided")
            vocabularyMeaningRepository.save(
                VocabularyMeaning(
                    vocabulary = saved,
                    languageCode = request.languageCode!!,
                    meaningText = request.meaningText,
                    exampleSentence = request.exampleSentence,
                    exampleTranslation = request.exampleTranslation,
                    partOfSpeech = pos,
                    senseOrder = 1,
                )
            )
        } else {
            null
        }

        val media = if (request.mediaType != null || !request.mediaUrl.isNullOrBlank()) {
            val mediaType = request.mediaType
                ?: throw BaseException.BadRequestException("'media_type' is required when media_url is provided")
            val mediaUrl = request.mediaUrl
                ?: throw BaseException.BadRequestException("'media_url' is required when media_type is provided")
            vocabularyMediaRepository.save(
                VocabularyMedia(
                    vocabulary = saved,
                    mediaType = mediaType,
                    url = mediaUrl,
                )
            )
        } else {
            null
        }

        request.questionType?.let { questionType ->
            val termId = term.id ?: 0L
            val meaningId = meaning?.id
            val mediaId = media?.id
            val (questionRefId, answerRefId) = when (questionType) {
                com.exe.vocafy_BE.enum.VocabularyQuestionType.LOOK_TERM_SELECT_MEANING -> {
                    if (meaningId == null) {
                        throw BaseException.BadRequestException("meaning_text is required for question_type")
                    }
                    termId to meaningId
                }
                com.exe.vocafy_BE.enum.VocabularyQuestionType.LISTEN_SELECT_TERM -> {
                    if (mediaId == null) {
                        throw BaseException.BadRequestException("media is required for question_type")
                    }
                    mediaId to termId
                }
                com.exe.vocafy_BE.enum.VocabularyQuestionType.LOOK_MEANING_INPUT_TERM -> {
                    if (meaningId == null) {
                        throw BaseException.BadRequestException("meaning_text is required for question_type")
                    }
                    meaningId to termId
                }
                com.exe.vocafy_BE.enum.VocabularyQuestionType.LOOK_IMAGE_SELECT_TERM -> {
                    if (mediaId == null) {
                        throw BaseException.BadRequestException("media is required for question_type")
                    }
                    mediaId to termId
                }
            }
            vocabularyQuestionRepository.save(
                VocabularyQuestion(
                    vocabulary = saved,
                    questionType = questionType,
                    questionRefId = questionRefId,
                    answerRefId = answerRefId,
                    difficultyLevel = request.difficultyLevel ?: 1,
                )
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
    override fun listByUserId(userId: UUID, pageable: Pageable): ServiceResult<PageResponse<VocabularyResponse>> {
        val requester = currentUser()
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
        val user = currentUser()
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
    override fun delete(id: Long): ServiceResult<Unit> {
        val entity = vocabularyRepository.findById(id)
            .orElseThrow { BaseException.NotFoundException("Vocabulary not found") }

        vocabularyTermRepository.deleteAllByVocabularyId(id)
        vocabularyMeaningRepository.deleteAllByVocabularyId(id)
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

    private fun currentUser(): User {
        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw BaseException.UnauthorizedException("Unauthorized")
        val jwt = authentication.principal as? Jwt
            ?: throw BaseException.UnauthorizedException("Unauthorized")
        val subject = jwt.subject ?: throw BaseException.BadRequestException("Invalid user_id")

        val parsed = runCatching { UUID.fromString(subject) }.getOrNull()
        if (parsed != null) {
            return userRepository.findById(parsed)
                .orElseThrow { BaseException.NotFoundException("User not found") }
        }
        return userRepository.findByEmail(subject)
            ?: throw BaseException.NotFoundException("User not found")
    }
}
