package com.exe.vocafy_BE.implement

import com.exe.vocafy_BE.handler.BaseException
import com.exe.vocafy_BE.mapper.VocabularyMapper
import com.exe.vocafy_BE.model.dto.request.VocabularyCreateRequest
import com.exe.vocafy_BE.model.dto.request.VocabularyUpdateRequest
import com.exe.vocafy_BE.model.dto.response.ServiceResult
import com.exe.vocafy_BE.model.dto.response.VocabularyResponse
import com.exe.vocafy_BE.repo.VocabularyRepository
import com.exe.vocafy_BE.service.VocabularyService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class VocabularyServiceImpl(
    private val vocabularyRepository: VocabularyRepository,
) : VocabularyService {

    @Transactional
    override fun create(request: VocabularyCreateRequest): ServiceResult<VocabularyResponse> {
        val saved = vocabularyRepository.save(VocabularyMapper.toEntity(request))
        return ServiceResult(
            message = "Created",
            result = VocabularyMapper.toResponse(saved),
        )
    }

    @Transactional(readOnly = true)
    override fun getById(id: Long): ServiceResult<VocabularyResponse> {
        val entity = vocabularyRepository.findById(id)
            .orElseThrow { BaseException.NotFoundException("Vocabulary not found") }
        return ServiceResult(
            message = "Ok",
            result = VocabularyMapper.toResponse(entity),
        )
    }

    @Transactional(readOnly = true)
    override fun list(): ServiceResult<List<VocabularyResponse>> {
        val items = vocabularyRepository.findAll().map(VocabularyMapper::toResponse)
        return ServiceResult(
            message = "Ok",
            result = items,
        )
    }

    @Transactional
    override fun update(id: Long, request: VocabularyUpdateRequest): ServiceResult<VocabularyResponse> {
        val entity = vocabularyRepository.findById(id)
            .orElseThrow { BaseException.NotFoundException("Vocabulary not found") }
        val updated = vocabularyRepository.save(VocabularyMapper.applyUpdate(entity, request))
        return ServiceResult(
            message = "Updated",
            result = VocabularyMapper.toResponse(updated),
        )
    }
}
