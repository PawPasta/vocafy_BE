package com.exe.vocafy_BE.implement

import com.exe.vocafy_BE.handler.BaseException
import com.exe.vocafy_BE.mapper.VocabularyMapper
import com.exe.vocafy_BE.model.dto.request.VocabularyCreateRequest
import com.exe.vocafy_BE.model.dto.request.VocabularyUpdateRequest
import com.exe.vocafy_BE.model.dto.response.ServiceResult
import com.exe.vocafy_BE.model.dto.response.VocabularyMeaningResponse
import com.exe.vocafy_BE.model.dto.response.VocabularyMediaResponse
import com.exe.vocafy_BE.model.dto.response.VocabularyResponse
import com.exe.vocafy_BE.model.dto.response.VocabularyTermResponse
import com.exe.vocafy_BE.model.entity.VocabularyMeaning
import com.exe.vocafy_BE.model.entity.VocabularyMedia
import com.exe.vocafy_BE.model.entity.VocabularyTerm
import com.exe.vocafy_BE.repo.CourseRepository
import com.exe.vocafy_BE.repo.VocabularyMeaningRepository
import com.exe.vocafy_BE.repo.VocabularyMediaRepository
import com.exe.vocafy_BE.repo.VocabularyRepository
import com.exe.vocafy_BE.repo.VocabularyTermRepository
import com.exe.vocafy_BE.service.VocabularyService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class VocabularyServiceImpl(
    private val vocabularyRepository: VocabularyRepository,
    private val courseRepository: CourseRepository,
    private val vocabularyTermRepository: VocabularyTermRepository,
    private val vocabularyMeaningRepository: VocabularyMeaningRepository,
    private val vocabularyMediaRepository: VocabularyMediaRepository,
) : VocabularyService {

    @Transactional
    override fun create(request: VocabularyCreateRequest): ServiceResult<VocabularyResponse> {
        val course = courseRepository.findById(request.courseId ?: 0)
            .orElseThrow { BaseException.NotFoundException("Course not found") }
        val saved = vocabularyRepository.save(VocabularyMapper.toEntity(request, course))
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
    override fun list(): ServiceResult<List<VocabularyResponse>> {
        val items = vocabularyRepository.findAll().map { buildResponse(it) }
        return ServiceResult(
            message = "Ok",
            result = items,
        )
    }

    @Transactional
    override fun update(id: Long, request: VocabularyUpdateRequest): ServiceResult<VocabularyResponse> {
        val entity = vocabularyRepository.findById(id)
            .orElseThrow { BaseException.NotFoundException("Vocabulary not found") }
        val course = courseRepository.findById(request.courseId ?: 0)
            .orElseThrow { BaseException.NotFoundException("Course not found") }
        val updated = vocabularyRepository.save(VocabularyMapper.applyUpdate(entity, request, course))
        replaceChildren(updated.id ?: 0, request)
        return ServiceResult(
            message = "Updated",
            result = buildResponse(updated),
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
            )
        }
        val medias = vocabularyMediaRepository.findAllByVocabularyIdOrderByIdAsc(vocabId).map {
            VocabularyMediaResponse(
                id = it.id ?: 0,
                mediaType = it.mediaType,
                url = it.url,
                meta = it.meta,
                createdAt = it.createdAt,
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
