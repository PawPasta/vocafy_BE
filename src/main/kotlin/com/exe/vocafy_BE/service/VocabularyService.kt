package com.exe.vocafy_BE.service

import com.exe.vocafy_BE.model.dto.request.VocabularyCreateRequest
import com.exe.vocafy_BE.model.dto.request.VocabularyUpdateRequest
import com.exe.vocafy_BE.model.dto.response.PageResponse
import com.exe.vocafy_BE.model.dto.response.ServiceResult
import com.exe.vocafy_BE.model.dto.response.VocabularyResponse
import org.springframework.data.domain.Pageable

interface VocabularyService {
    fun create(request: VocabularyCreateRequest): ServiceResult<VocabularyResponse>
    fun getById(id: Long): ServiceResult<VocabularyResponse>
    fun list(pageable: Pageable): ServiceResult<PageResponse<VocabularyResponse>>
    fun listByCourseId(courseId: Long, pageable: Pageable): ServiceResult<PageResponse<VocabularyResponse>>
    fun update(id: Long, request: VocabularyUpdateRequest): ServiceResult<VocabularyResponse>
    fun delete(id: Long): ServiceResult<Unit>
}
