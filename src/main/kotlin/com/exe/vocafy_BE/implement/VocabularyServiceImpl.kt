package com.exe.vocafy_BE.implement

import com.exe.vocafy_BE.handler.BaseException
import com.exe.vocafy_BE.mapper.VocabularyMapper
import com.exe.vocafy_BE.model.dto.request.VocabularyCreateRequest
import com.exe.vocafy_BE.model.dto.request.VocabularyUpdateRequest
import com.exe.vocafy_BE.model.dto.response.PageResponse
import com.exe.vocafy_BE.model.dto.response.ServiceResult
import com.exe.vocafy_BE.model.dto.response.VocabularyMeaningResponse
import com.exe.vocafy_BE.model.dto.response.VocabularyMediaResponse
import com.exe.vocafy_BE.model.dto.response.VocabularyResponse
import com.exe.vocafy_BE.model.dto.response.VocabularyTermResponse
import com.exe.vocafy_BE.model.entity.VocabularyMeaning
import com.exe.vocafy_BE.model.entity.VocabularyMedia
import com.exe.vocafy_BE.model.entity.VocabularyTerm
import com.exe.vocafy_BE.repo.VocabularyMeaningRepository
import com.exe.vocafy_BE.repo.VocabularyMediaRepository
import com.exe.vocafy_BE.repo.VocabularyRepository
import com.exe.vocafy_BE.repo.VocabularyTermRepository
import com.exe.vocafy_BE.service.VocabularyService
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class VocabularyServiceImpl(
    private val vocabularyRepository: VocabularyRepository,
    private val vocabularyTermRepository: VocabularyTermRepository,
    private val vocabularyMeaningRepository: VocabularyMeaningRepository,
    private val vocabularyMediaRepository: VocabularyMediaRepository,
) : VocabularyService {

    @Transactional
    override fun create(request: VocabularyCreateRequest): ServiceResult<VocabularyResponse> {
        val saved = vocabularyRepository.save(VocabularyMapper.toEntity(request))
        saveChildren(saved.id ?: 0, request)
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
        val page = vocabularyRepository.findAllByCourseId(courseId, pageable)
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

        vocabularyRepository.delete(entity)

        return ServiceResult(
            message = "Deleted",
            result = Unit,
        )
    }

    private fun buildResponse(entity: com.exe.vocafy_BE.model.entity.Vocabulary): VocabularyResponse {
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
        return VocabularyMapper.toResponse(entity, terms, meanings, medias)
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
}
